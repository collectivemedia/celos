package com.collective.celos.ci;

import com.collective.celos.cd.CelosCd;
import com.collective.celos.cd.config.CelosCdContext;
import com.collective.celos.cd.deployer.JScpWorker;
import com.collective.celos.cd.fixtures.FixturesHdfsWorkerManager;
import com.collective.celos.ci.config.TestContext;
import com.collective.celos.ci.config.TestContextBuilder;
import com.collective.celos.ci.fixtures.PlainFixtureComparator;
import com.collective.celos.server.CelosServer;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.Selectors;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CelosCi {

    public static final String WORKFLOW_DIR_CELOS_PATH = "workflows";
    public static final String DEFAULTS_DIR_CELOS_PATH = "defaults";
    public static final String DB_DIR_CELOS_PATH = "db";
    public static final String HDFS_PREFIX = "HDFS_PREFIX";

    private static final String LOCAL_OUTPUT_PATTERN = "%s/output";

    public static void main(String... args) throws Exception {

        TestContextBuilder contextBuilder = new TestContextBuilder();
        TestContext testContext = contextBuilder.parse(args);
        if (testContext == null) {
            contextBuilder.printHelp(80, 5, 3, true, System.out);
        } else {
            prepareEnvironment(testContext);
            runServer(testContext);
            compareOutputs(testContext);
        }
    }

    private static void compareOutputs(TestContext testContext) throws Exception {
        CelosCdContext celosCdContext = testContext.createCelosCdContext();
        FixturesHdfsWorkerManager manager = new FixturesHdfsWorkerManager(celosCdContext, ImmutableMap.of("PLAIN", new PlainFixtureComparator(celosCdContext)));
        manager.processLocalDir(String.format(LOCAL_OUTPUT_PATTERN, testContext.getDeployDir()));
    }

    private static void prepareEnvironment(TestContext testContext) throws IOException, URISyntaxException {
        Path tempDir = Files.createTempDirectory("celos");
        File celosWorkDir = tempDir.toFile();
        FileUtils.forceDeleteOnExit(celosWorkDir);

        System.out.println("Celos created at: " + tempDir.toAbsolutePath().toString());
        System.out.println("HDFS prefix is: " + testContext.getHdfsPrefix());

        File workflowDir = new File(celosWorkDir, WORKFLOW_DIR_CELOS_PATH);
        File defaultsDir = new File(celosWorkDir, DEFAULTS_DIR_CELOS_PATH);
        File dbDir = new File(celosWorkDir, DB_DIR_CELOS_PATH);

        workflowDir.mkdirs();
        defaultsDir.mkdirs();
        dbDir.mkdirs();

        testContext.setCelosWorkflowDirUri(workflowDir);
        testContext.setCelosDefaultsDirUri(defaultsDir);
        testContext.setCelosDbDirUri(dbDir);

        JScpWorker worker = new JScpWorker(testContext.getUserName(), testContext.getTarget().getScpSecuritySettings());
        FileObject remoteDefaultsFile = worker.getFileObjectByUri(testContext.getTarget().getDefaultsFile());
        if (remoteDefaultsFile.exists()) {
            FileObject localDefaultsFile = worker.getFileObjectByUri(new File(defaultsDir, remoteDefaultsFile.getName().getBaseName()).toString());
            localDefaultsFile.copyFrom(remoteDefaultsFile, Selectors.SELECT_SELF);
        }
    }

    private static void runServer(TestContext testContext) throws Exception {
        final CelosServer celosServer = new CelosServer();

        try {

            Integer port = celosServer.startServer(
                    ImmutableMap.of(HDFS_PREFIX, testContext.getHdfsPrefix()),
                    testContext.getCelosWorkflowDirUri().toString(),
                    testContext.getCelosDefaultsDirUri().toString(),
                    testContext.getCelosDbDirUri().toString()
            );

            testContext.setCelosPort(port);

            CelosCdContext celosCdContext = testContext.createCelosCdContext();

            System.out.println("Deploying workflow " + testContext.getWorkflowName());
            CelosCd.runForContext(celosCdContext);

            new CelosSchedulerRunner(testContext).runCelosScheduler();
        } finally {
            System.out.println("Job is finished");
            celosServer.stopServer();
        }
    }


}
