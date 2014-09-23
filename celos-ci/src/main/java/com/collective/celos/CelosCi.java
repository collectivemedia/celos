package com.collective.celos;

import com.collective.celos.config.ci.CelosCiContext;
import com.collective.celos.config.ci.CelosCiContextBuilder;
import com.collective.celos.deploy.HdfsDeployer;
import com.collective.celos.deploy.JScpWorker;
import com.collective.celos.deploy.WorkflowFileDeployer;
import com.collective.celos.fixtures.FixturesHdfsWorkerManager;
import com.collective.celos.fixtures.compare.PlainFixtureComparatorWorker;
import com.collective.celos.fixtures.deploy.PlainFixtureDeployWorker;
import com.collective.celos.server.CelosServer;
import com.collective.celos.testmode.CelosSchedulerRunner;
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
    private static final String LOCAL_INPUT_PATTERN = "%s/input";

    private WorkflowFileDeployer wfDeployer;
    private HdfsDeployer hdfsDeployer;


    public static void main(String... args) throws Exception {

//        args = "--deployDir /home/akonopko/work/celos2/samples/wordcount2 --target sftp://107.170.177.172/home/akonopko/target.json --workflowName wordcount".split(" ");
//        args = "--deployDir /home/akonopko/work/celos2/samples/wordcount --target sftp://celos001/home/akonopko/target.json --workflowName wordcount --mode TEST".split(" ");

        new CelosCi().runCelosCi(args);
    }

    private void runCelosCi(String[] args) throws Exception {
        CelosCiContextBuilder contextBuilder = new CelosCiContextBuilder();
        CelosCiContext ciContext = contextBuilder.parse(args);
        if (ciContext == null) {
            contextBuilder.printHelp(80, 5, 3, true, System.out);
        } else {
            wfDeployer = new WorkflowFileDeployer(ciContext);
            hdfsDeployer = new HdfsDeployer(ciContext);
            if (ciContext.getMode() == CelosCiContext.Mode.DEPLOY) {
                doDeploy();
            } else if (ciContext.getMode() == CelosCiContext.Mode.UNDEPLOY) {
                doUndeploy();
            } else if (ciContext.getMode() == CelosCiContext.Mode.TEST) {
                prepareCelosServerEnv(ciContext);
                for (File testCase : new File("src/test").listFiles()) {
                    System.out.println("Running test case " + testCase.getName());
                    loadHdfsInputs(String.format(LOCAL_INPUT_PATTERN, testCase.getAbsolutePath()), ciContext);
                    runCelosServer(ciContext);
                    compareHdfsOutputs(String.format(LOCAL_OUTPUT_PATTERN, testCase.getAbsolutePath()), ciContext);
                }
            }
        }
    }



    private void doUndeploy() throws Exception {
        wfDeployer.undeploy();
        hdfsDeployer.undeploy();
    }

    private void doDeploy() throws Exception {
        wfDeployer.deploy();
        hdfsDeployer.deploy();
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

    private void prepareCelosServerEnv(CelosCiContext ciContext) throws IOException, URISyntaxException {
        Path tempDir = Files.createTempDirectory("celos");
        File celosWorkDir = tempDir.toFile();
        FileUtils.forceDeleteOnExit(celosWorkDir);

        System.out.println("Celos created at: " + tempDir.toAbsolutePath().toString());
        System.out.println("HDFS prefix is: " + ciContext.getHdfsPrefix());

        File workflowDir = new File(celosWorkDir, WORKFLOW_DIR_CELOS_PATH);
        File defaultsDir = new File(celosWorkDir, DEFAULTS_DIR_CELOS_PATH);
        File dbDir = new File(celosWorkDir, DB_DIR_CELOS_PATH);

        workflowDir.mkdirs();
        defaultsDir.mkdirs();
        dbDir.mkdirs();

        JScpWorker worker = new JScpWorker(ciContext.getUserName(), ciContext.getTarget().getScpSecuritySettings());
        FileObject remoteDefaultsFile = worker.getFileObjectByUri(ciContext.getTarget().getDefaultsFile());
        if (remoteDefaultsFile.exists()) {
            FileObject localDefaultsFile = worker.getFileObjectByUri(new File(defaultsDir, remoteDefaultsFile.getName().getBaseName()).toString());
            localDefaultsFile.copyFrom(remoteDefaultsFile, Selectors.SELECT_SELF);
        }
    }

    private void runCelosServer(CelosCiContext ciContext) throws Exception {
        final CelosServer celosServer = new CelosServer();

        try {

            Integer port = celosServer.startServer(
                    ImmutableMap.of(HDFS_PREFIX, ciContext.getHdfsPrefix()),
                    ciContext.getTestContext().getCelosWorkflowDir().toString(),
                    ciContext.getTestContext().getCelosDefaultsDir().toString(),
                    ciContext.getTestContext().getCelosDbDir().toString()
            );


            System.out.println("Deploying workflow " + ciContext.getWorkflowName());
            doDeploy();

            new CelosSchedulerRunner(port).runCelosScheduler();
        } finally {
            System.out.println("Stopping Celos");
            celosServer.stopServer();
        }
    }



}
