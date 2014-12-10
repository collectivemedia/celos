package com.collective.celos.ci.mode.test;

import com.collective.celos.Util;
import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.deploy.HdfsDeployer;
import com.collective.celos.ci.deploy.JScpWorker;
import com.collective.celos.ci.deploy.WorkflowFileDeployer;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.FixtureComparer;
import com.collective.celos.ci.testing.fixtures.deploy.FixtureDeployer;
import com.collective.celos.server.CelosServer;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestRun {

    private static final String HDFS_PREFIX_PATTERN = "/user/%s/test/%s/%s";
    private static final String HDFS_PREFIX_JS_VAR = "HDFS_PREFIX_JS_VAR";
    private static final String CELOS_USER_JS_VAR = "CELOS_USER_JS_VAR";
    private static final String WORKFLOW_DIR_CELOS_PATH = "workflows";
    private static final String DEFAULTS_DIR_CELOS_PATH = "defaults";
    private static final String DB_DIR_CELOS_PATH = "db";

    private final WorkflowFileDeployer wfDeployer;
    private final HdfsDeployer hdfsDeployer;
    private final CelosCiContext ciContext;
    private final File celosWorkflowDir;
    private final File celosDefaultsDir;
    private final File celosDbDir;
    private final File celosTempDir;
    private final String hdfsPrefix;
    private final TestCase testCase;
    private final File testCasesDir;

    public TestRun(CelosCiTarget target, CelosCiCommandLine commandLine, TestCase testCase) throws Exception {

        this.testCase = testCase;
        this.celosTempDir = Files.createTempDirectory("celos").toFile();
        this.testCasesDir = commandLine.getTestCasesDir();

        String testUUID = UUID.randomUUID().toString();
        String hdfsPrefix = String.format(HDFS_PREFIX_PATTERN, commandLine.getUserName(), commandLine.getWorkflowName(), testUUID);

        this.hdfsPrefix = Util.requireNonNull(hdfsPrefix);
        this.celosWorkflowDir = new File(celosTempDir, WORKFLOW_DIR_CELOS_PATH);
        this.celosDefaultsDir = new File(celosTempDir, DEFAULTS_DIR_CELOS_PATH);
        this.celosDbDir = new File(celosTempDir, DB_DIR_CELOS_PATH);

        CelosCiTarget testTarget = new CelosCiTarget(target.getPathToHdfsSite(), target.getPathToCoreSite(), celosWorkflowDir.toURI(), target.getDefaultsFile());
        this.ciContext = new CelosCiContext(testTarget, commandLine.getUserName(), CelosCiContext.Mode.TEST, commandLine.getDeployDir(), commandLine.getWorkflowName(), hdfsPrefix);

        this.wfDeployer = new WorkflowFileDeployer(ciContext);
        this.hdfsDeployer = new HdfsDeployer(ciContext);
    }

    public File getCelosWorkflowDir() {
        return celosWorkflowDir;
    }

    public File getCelosDefaultsDir() {
        return celosDefaultsDir;
    }

    public File getCelosDbDir() {
        return celosDbDir;
    }

    public File getCelosTempDir() {
        return celosTempDir;
    }

    public File getTestCasesDir() {
        return testCasesDir;
    }

    public String getHdfsPrefix() {
        return hdfsPrefix;
    }

    public CelosCiContext getCiContext() {
        return ciContext;
    }

    public void start() throws Exception {
        prepareCelosServerEnv();

        System.out.println("Test case " + testCase.getName() + ": temp dir for Celos is " + celosTempDir.getAbsolutePath().toString());
        System.out.println("Test case " + testCase.getName() + ": HDFS prefix is: " + hdfsPrefix);

        wfDeployer.deploy();
        hdfsDeployer.deploy();

        Map<String, String> additionalJSParams = ImmutableMap.of(
                HDFS_PREFIX_JS_VAR, hdfsPrefix,
                CELOS_USER_JS_VAR, ciContext.getUserName());

        final CelosServer celosServer = new CelosServer();
        try {
            Integer port = celosServer.startServer(
                    additionalJSParams,
                    celosWorkflowDir.toString(),
                    celosDefaultsDir.toString(),
                    celosDbDir.toString()
            );

            System.out.println("Running test case " + testCase.getName());
            for (FixtureDeployer fixtureDeployer : testCase.getInputs()) {
                fixtureDeployer.deploy(this);
            }
            new CelosSchedulerWorker(port, testCase.getWorkflows()).runCelosScheduler(testCase);
            for (FixtureComparer fixtureComparer : testCase.getOutputs()) {
                FixObjectCompareResult result = fixtureComparer.check(this);
                if (result.getStatus() == FixObjectCompareResult.Status.FAIL) {
                    System.err.println(result.generateDescription());
                }
            }
        } finally {
            System.out.println("Stopping Celos");
            FileUtils.forceDelete(celosTempDir);
            ciContext.getFileSystem().delete(new org.apache.hadoop.fs.Path(ciContext.getHdfsPrefix()), true);
            celosServer.stopServer();
        }

    }

    public TestCase getTestCase() {
        return testCase;
    }

    private void prepareCelosServerEnv() throws IOException, URISyntaxException {

        celosWorkflowDir.mkdirs();
        celosDefaultsDir.mkdirs();
        celosDbDir.mkdirs();

        JScpWorker worker = new JScpWorker(ciContext.getUserName());
        FileObject remoteDefaultsFile = worker.getFileObjectByUri(ciContext.getTarget().getDefaultsFile());
        if (remoteDefaultsFile.exists()) {
            FileObject localDefaultsFile = worker.getFileObjectByUri(new File(celosDefaultsDir, remoteDefaultsFile.getName().getBaseName()).toURI());
            localDefaultsFile.copyFrom(remoteDefaultsFile, Selectors.SELECT_SELF);
        }
    }

}
