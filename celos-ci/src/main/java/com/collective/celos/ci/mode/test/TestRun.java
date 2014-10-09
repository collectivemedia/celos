package com.collective.celos.ci.mode.test;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.testing.TestContext;
import com.collective.celos.ci.deploy.HdfsDeployer;
import com.collective.celos.ci.deploy.JScpWorker;
import com.collective.celos.ci.deploy.WorkflowFileDeployer;
import com.collective.celos.ci.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.fixtures.deploy.FixDirDeployer;
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
import java.util.List;
import java.util.UUID;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestRun {

    private static final String HDFS_PREFIX_PATTERN = "/user/%s/test/%s/%s";
    private static final String HDFS_PREFIX = "HDFS_PREFIX";
    private static final String LOCAL_OUTPUT_PATTERN = "%s/output";
    private static final String LOCAL_INPUT_PATTERN = "%s/input";

    private final WorkflowFileDeployer wfDeployer;
    private final HdfsDeployer hdfsDeployer;
    private final CelosCiContext ciContext;
    private final TestContext testContext;
    private final List<FixDirDeployer> deployers;
    private final List<FixObjectComparer> fileComparers;
    private final String testCaseName;

    public TestRun(CelosCiTarget target, String username, String workflowName,
                   File deployDir, String testCase, List<FixDirDeployer> deployers, List<FixObjectComparer> fileComparers) throws Exception {

        File celosTempDir = Files.createTempDirectory("celos").toFile();

        System.out.println("Test case " + testCase + ": temp dir for Celos is " + celosTempDir.getAbsolutePath().toString());
        String hdfsPrefix = String.format(HDFS_PREFIX_PATTERN, username, workflowName, UUID.randomUUID().toString());
        System.out.println("Test case " + testCase + ": HDFS prefix is: " + hdfsPrefix);

        this.deployers = deployers;
        this.fileComparers = fileComparers;
        this.testCaseName = testCase;
        this.testContext = new TestContext(celosTempDir, hdfsPrefix);
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

        wfDeployer.deploy();
        hdfsDeployer.deploy();

        final CelosServer celosServer = new CelosServer();
        try {
            Integer port = celosServer.startServer(
                    ImmutableMap.of(HDFS_PREFIX, testContext.getHdfsPrefix()),
                    testContext.getCelosWorkflowDir().toString(),
                    testContext.getCelosDefaultsDir().toString(),
                    testContext.getCelosDbDir().toString()
            );

            System.out.println("Running test case " + testCaseName);
//            File inputsDir = new File(String.format(LOCAL_INPUT_PATTERN, testCaseDir.getAbsolutePath()));
//            deployWorkerManager.processLocalDir(inputsDir);

//            new CelosSchedulerWorker(port).runCelosScheduler(new TestConfigParser().parse(testCaseDir));

//            File outputsDir = new File(String.format(LOCAL_OUTPUT_PATTERN, testCaseDir.getAbsolutePath()));
//            compareWorkerManager.processLocalDir(outputsDir);
        } finally {
            System.out.println("Stopping Celos");
            celosServer.stopServer();
        }

    }

    private void prepareCelosServerEnv(TestContext testContext, CelosCiContext ciContext) throws IOException, URISyntaxException {

        testContext.getCelosWorkflowDir().mkdirs();
        testContext.getCelosDefaultsDir().mkdirs();
        testContext.getCelosDbDir().mkdirs();
        FileUtils.forceDeleteOnExit(testContext.getCelosWorkDir());

        ciContext.getFileSystem().deleteOnExit(new org.apache.hadoop.fs.Path(ciContext.getHdfsPrefix()));

        JScpWorker worker = ciContext.getJscpWorker();
        FileObject remoteDefaultsFile = worker.getFileObjectByUri(ciContext.getTarget().getDefaultsFile());
        if (remoteDefaultsFile.exists()) {
            FileObject localDefaultsFile = worker.getFileObjectByUri(new File(testContext.getCelosDefaultsDir(), remoteDefaultsFile.getName().getBaseName()).toURI());
            localDefaultsFile.copyFrom(remoteDefaultsFile, Selectors.SELECT_SELF);
        }
    }

}
