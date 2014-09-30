package com.collective.celos.ci.testing;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.testing.TestConfigParser;
import com.collective.celos.ci.config.testing.TestContext;
import com.collective.celos.ci.deploy.HdfsDeployer;
import com.collective.celos.ci.deploy.JScpWorker;
import com.collective.celos.ci.fixtures.compare.PlainFixtureComparatorWorker;
import com.collective.celos.ci.fixtures.deploy.PlainFixtureDeployWorker;
import com.collective.celos.ci.deploy.WorkflowFileDeployer;
import com.collective.celos.ci.fixtures.FixturesHdfsWorkerManager;
import com.collective.celos.server.CelosServer;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class CelosCiIntegrationTestRunner {

    public static final String HDFS_PREFIX = "HDFS_PREFIX";

    private static final String LOCAL_OUTPUT_PATTERN = "%s/output";
    private static final String LOCAL_INPUT_PATTERN = "%s/input";
    private static final String TEST_CASES_PATTERN = "%s/celos-ci";

    private final WorkflowFileDeployer wfDeployer;
    private final HdfsDeployer hdfsDeployer;
    private final CelosCiContext ciContext;
    private final TestContext testContext;

    public CelosCiIntegrationTestRunner(CelosCiContext ciContext, TestContext testContext) throws Exception {
        this.wfDeployer = new WorkflowFileDeployer(ciContext);
        this.hdfsDeployer = new HdfsDeployer(ciContext);
        this.ciContext = ciContext;
        this.testContext = testContext;
    }

    public void runTests() throws Exception {
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
            TestConfigParser configParser = new TestConfigParser();
            String testCasesDirPath = String.format(TEST_CASES_PATTERN, testContext.getTestCasesDir());
            for (File testCase : new File(testCasesDirPath).listFiles()) {
                System.out.println("Running test case " + testCase.getName());
                loadHdfsInputs(String.format(LOCAL_INPUT_PATTERN, testCase.getAbsolutePath()), ciContext);
                new CelosSchedulerWorker(port).runCelosScheduler(configParser.parse(testCase));
                compareHdfsOutputs(String.format(LOCAL_OUTPUT_PATTERN, testCase.getAbsolutePath()), ciContext);
                FileUtils.cleanDirectory(testContext.getCelosDbDir());
            }
        } finally {
            System.out.println("Stopping Celos");
            celosServer.stopServer();
        }
    }


    private void loadHdfsInputs(String localPath, CelosCiContext celosCiContext) throws Exception {

        FixturesHdfsWorkerManager manager = new FixturesHdfsWorkerManager(celosCiContext, ImmutableMap.of("PLAIN", new PlainFixtureDeployWorker()));
        manager.processLocalDir(localPath);
    }

    private void compareHdfsOutputs(String localPath, CelosCiContext celosCiContext) throws Exception {
        FixturesHdfsWorkerManager manager = new FixturesHdfsWorkerManager(celosCiContext, ImmutableMap.of("PLAIN", new PlainFixtureComparatorWorker()));
        manager.processLocalDir(localPath);
        System.out.println("Output data fits fixtures");
    }

    private void prepareCelosServerEnv(TestContext testContext, CelosCiContext ciContext) throws IOException, URISyntaxException {

        testContext.getCelosWorkflowDir().mkdirs();
        testContext.getCelosDefaultsDir().mkdirs();
        testContext.getCelosDbDir().mkdirs();
        FileUtils.forceDeleteOnExit(testContext.getCelosWorkDir());

        JScpWorker worker = new JScpWorker(ciContext.getUserName(), ciContext.getTarget().getScpSecuritySettings());
        FileObject remoteDefaultsFile = worker.getFileObjectByUri(ciContext.getTarget().getDefaultsFile());
        if (remoteDefaultsFile.exists()) {
            FileObject localDefaultsFile = worker.getFileObjectByUri(new File(testContext.getCelosDefaultsDir(), remoteDefaultsFile.getName().getBaseName()).toString());
            localDefaultsFile.copyFrom(remoteDefaultsFile, Selectors.SELECT_SELF);
        }
    }


}

