/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos.ci.mode.test;

import com.collective.celos.CelosClient;
import com.collective.celos.ci.config.CiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.deploy.HdfsDeployer;
import com.collective.celos.ci.deploy.WorkflowFilesDeployer;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.FixtureComparer;
import com.collective.celos.ci.testing.fixtures.deploy.FixtureDeployer;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestRun {

    private final CelosCiContext ciContext;
    private final File testCaseTempDir;
    private final TestCase testCase;
    private final File testCasesDir;
    private final UUID testUUID;
    private final boolean keepTempData;
    private final CelosCiTarget originalTarget;
    private final TestRunCelosServerMode celosServerMode;

    public TestRun(CelosCiTarget target, CiCommandLine commandLine, TestCase testCase, File celosCiTempDir) throws Exception {

        this.testCase = testCase;
        this.testCasesDir = commandLine.getTestCasesDir();

        this.testUUID = UUID.randomUUID();
        this.testCaseTempDir = new File(celosCiTempDir, testUUID.toString());

        this.originalTarget = target;
        if (commandLine.getCelosServerUrl() != null) {
            this.celosServerMode = new TestRunCelosServerModeProvided(commandLine, target);
        } else {
            this.celosServerMode = new TestRunCelosServerModeEmbedded(commandLine, testCaseTempDir, testUUID);
        }

        CelosCiTarget testTarget = new CelosCiTarget(target.getPathToHdfsSite(), target.getPathToCoreSite(), celosServerMode.getCelosWorkflowDir(), celosServerMode.getCelosDefaultsDir(), target.getHiveJdbc());
        this.ciContext = new CelosCiContext(testTarget, commandLine.getUserName(), CelosCiContext.Mode.TEST, commandLine.getDeployDir(), commandLine.getWorkflowName(), celosServerMode.getHdfsPrefix(), commandLine.getHdfsRoot());

        this.keepTempData = commandLine.isKeepTempData();
    }

    public UUID getTestUUID() {
        return testUUID;
    }

    public File getTestCaseTempDir() {
        return testCaseTempDir;
    }

    public File getTestCasesDir() {
        return testCasesDir;
    }

    public CelosCiContext getCiContext() {
        return ciContext;
    }

    public void start() throws Exception {

        List<FixObjectCompareResult> results = executeTestRun();

        if (isTestRunFailed(results)) {
            throw new TestRunFailedException(getComparisonResults(results));
        } else {
            System.out.println(testCase.getName() + ": Real and expected fixtures matched");
        }

    }

    private List<FixObjectCompareResult> executeTestRun() throws Exception {

        try {
            URI address = celosServerMode.startServer(this);

            System.out.println("Running test case " + testCase.getName());
            if (testCaseTempDir.exists()) {
                System.out.println(testCase.getName() + ": temp dir for Celos is " + testCaseTempDir.getAbsolutePath().toString());
            }
            System.out.println(testCase.getName() + ": HDFS prefix is: " + celosServerMode.getHdfsPrefix());

            new WorkflowFilesDeployer(ciContext).deploy();
            new HdfsDeployer(ciContext).deploy();

            for (FixtureDeployer fixtureDeployer : testCase.getInputs()) {
                fixtureDeployer.deploy(this);
            }
            CelosClient client = new CelosClient(address);
            new CelosSchedulerWorker(client).runCelosScheduler(testCase);

            List<FixObjectCompareResult> results = Lists.newArrayList();
            for (FixtureComparer fixtureComparer : testCase.getOutputs()) {
                results.add(fixtureComparer.check(this));
            }
            return results;
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            throw t;
        } finally {
            celosServerMode.stopServer(this);
            cleanData();
        }
    }

    private void cleanData() {
        try {
            if (!keepTempData) {
                doCleanup();
                validateCleanState();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void validateCleanState() throws Exception {
        for (FixtureDeployer fixtureDeployer : testCase.getInputs()) {
            fixtureDeployer.validate(this);
        }
    }

    private boolean isTestRunFailed(List<FixObjectCompareResult> results) {
        for (FixObjectCompareResult result : results) {
            if (result.getStatus() == FixObjectCompareResult.Status.FAIL) {
                return true;
            }
        }
        return false;
    }

    private String getComparisonResults(List<FixObjectCompareResult> results) throws IOException {
        List<String> messages = Lists.newArrayList();
        for (FixObjectCompareResult result : results) {
            if (result.getStatus() == FixObjectCompareResult.Status.FAIL) {
                messages.add(result.generateDescription());
            }
        }
        return StringUtils.join(messages, "\n");
    }

    private void doCleanup() throws Exception {
        if (testCaseTempDir.exists()) {
            FileUtils.forceDelete(testCaseTempDir);
        }
        for (FixtureDeployer fixtureDeployer : testCase.getInputs()) {
            fixtureDeployer.undeploy(this);
        }
        ciContext.getFileSystem().delete(new org.apache.hadoop.fs.Path(ciContext.getHdfsPrefix()), true);
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public CelosCiTarget getOriginalTarget() {
        return originalTarget;
    }

    public String getHdfsPrefix() {
        return celosServerMode.getHdfsPrefix();
    }
}
