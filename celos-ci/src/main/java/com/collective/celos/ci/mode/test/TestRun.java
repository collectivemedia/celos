package com.collective.celos.ci.mode.test;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.testing.TestContext;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestRun {

    private static final String HDFS_PREFIX_PATTERN = "/user/%s/test/%s/%s";
    private static final String HDFS_PREFIX = "HDFS_PREFIX";
    public static final String CELOS_USER = "CELOS_USER";

    private final WorkflowFileDeployer wfDeployer;
    private final HdfsDeployer hdfsDeployer;
    private final CelosCiContext ciContext;
    private final TestContext testContext;
    private final File celosTempDir;
    private final TestCase testCase;

    public TestRun(CelosCiTarget target, String username, String workflowName, File deployDir, TestCase testCase) throws Exception {

        this.testCase = testCase;
        this.celosTempDir = Files.createTempDirectory("celos").toFile();
        String testUUID = UUID.randomUUID().toString();
        String hdfsPrefix = String.format(HDFS_PREFIX_PATTERN, username, workflowName, testUUID);
        this.testContext = new TestContext(celosTempDir, hdfsPrefix, testUUID, testCase);

        URI substitutedCelosWorkflowDir = testContext.getCelosWorkflowDir().toURI();
        CelosCiTarget testTarget = new CelosCiTarget(target.getPathToHdfsSite(), target.getPathToCoreSite(), substitutedCelosWorkflowDir, target.getDefaultsFile());
        this.ciContext = new CelosCiContext(testTarget, username, CelosCiContext.Mode.TEST, deployDir, workflowName, hdfsPrefix);

        this.wfDeployer = new WorkflowFileDeployer(ciContext);
        this.hdfsDeployer = new HdfsDeployer(ciContext);
    }

    public CelosCiContext getCiContext() {
        return ciContext;
    }

    public TestContext getTestContext() {
        return testContext;
    }

    public void start() throws Exception {
        prepareCelosServerEnv(testContext, ciContext);

        System.out.println("Test case " + testContext.getTestCase().getName() + ": temp dir for Celos is " + celosTempDir.getAbsolutePath().toString());
        System.out.println("Test case " + testContext.getTestCase().getName() + ": HDFS prefix is: " + testContext.getHdfsPrefix());

        wfDeployer.deploy();
        hdfsDeployer.deploy();

        Map<String, String> additionalJSParams = ImmutableMap.of(
                HDFS_PREFIX, testContext.getHdfsPrefix(),
                CELOS_USER, ciContext.getUserName());

        final CelosServer celosServer = new CelosServer();
        try {
            Integer port = celosServer.startServer(
                    additionalJSParams,
                    testContext.getCelosWorkflowDir().toString(),
                    testContext.getCelosDefaultsDir().toString(),
                    testContext.getCelosDbDir().toString()
            );

            System.out.println("Running test case " + testContext.getTestCase().getName());
            for (FixtureDeployer fixtureDeployer : testContext.getTestCase().getInputs()) {
                fixtureDeployer.deploy(testContext, ciContext);
            }
            new CelosSchedulerWorker(port).runCelosScheduler(testContext.getTestCase());
            for (FixtureComparer fixtureComparer : testContext.getTestCase().getOutputs()) {
                FixObjectCompareResult result = fixtureComparer.check(ciContext);
                if (result.getStatus() == FixObjectCompareResult.Status.FAIL) {
                    System.err.println(result.generateDescription());
                }
            }
        } finally {
            System.out.println("Stopping Celos");
            FileUtils.forceDelete(testContext.getCelosWorkDir());
            ciContext.getFileSystem().delete(new org.apache.hadoop.fs.Path(ciContext.getHdfsPrefix()), true);
            celosServer.stopServer();
        }

    }

    public TestCase getTestCase() {
        return testCase;
    }

    private void prepareCelosServerEnv(TestContext testContext, CelosCiContext ciContext) throws IOException, URISyntaxException {

        testContext.getCelosWorkflowDir().mkdirs();
        testContext.getCelosDefaultsDir().mkdirs();
        testContext.getCelosDbDir().mkdirs();

        JScpWorker worker = new JScpWorker(ciContext.getUserName());
        FileObject remoteDefaultsFile = worker.getFileObjectByUri(ciContext.getTarget().getDefaultsFile());
        if (remoteDefaultsFile.exists()) {
            FileObject localDefaultsFile = worker.getFileObjectByUri(new File(testContext.getCelosDefaultsDir(), remoteDefaultsFile.getName().getBaseName()).toURI());
            localDefaultsFile.copyFrom(remoteDefaultsFile, Selectors.SELECT_SELF);
        }
    }

}
