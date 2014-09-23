package com.collective.celos;

import com.collective.celos.cd.deployer.HdfsDeployer;
import com.collective.celos.cd.deployer.JScpWorker;
import com.collective.celos.cd.deployer.WorkflowFileDeployer;
import com.collective.celos.ci.CelosSchedulerRunner;
import com.collective.celos.ci.fixtures.PlainFixtureComparator;
import com.collective.celos.config.CelosCiContext;
import com.collective.celos.config.CelosCiContextBuilder;
import com.collective.celos.fixtures.FixturesHdfsWorkerManager;
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

//        args = "--deployDir /home/akonopko/work/celos2/samples/wordcount2 --target sftp://107.170.177.172/home/akonopko/target.json --workflowName wordcount".split(" ");
//        args = "--deployDir /home/akonopko/work/celos2/samples/wordcount --target sftp://celos001/home/akonopko/target.json --workflowName wordcount --mode TEST".split(" ");

        CelosCiContextBuilder contextBuilder = new CelosCiContextBuilder();
        CelosCiContext ciContext = contextBuilder.parse(args);
        if (ciContext == null) {
            contextBuilder.printHelp(80, 5, 3, true, System.out);
        } else {
            prepareEnvironment(ciContext);
            runServer(ciContext);
            compareOutputs(ciContext);
        }
    }

    private static void compareOutputs(CelosCiContext celosCiContext) throws Exception {
        FixturesHdfsWorkerManager manager = new FixturesHdfsWorkerManager(celosCiContext, ImmutableMap.of("PLAIN", new PlainFixtureComparator(celosCiContext)));
        manager.processLocalDir(String.format(LOCAL_OUTPUT_PATTERN, celosCiContext.getDeployDir()));
    }

    private static void prepareEnvironment(CelosCiContext ciContext) throws IOException, URISyntaxException {
        Path tempDir = Files.createTempDirectory("celos");
        File celosWorkDir = tempDir.toFile();
        FileUtils.forceDeleteOnExit(celosWorkDir);

        System.out.println("Celos created at: " + tempDir.toAbsolutePath().toString());
        System.out.println("HDFS prefix is: " + ciContext.getTestContext().getHdfsPrefix());

        File workflowDir = new File(celosWorkDir, WORKFLOW_DIR_CELOS_PATH);
        File defaultsDir = new File(celosWorkDir, DEFAULTS_DIR_CELOS_PATH);
        File dbDir = new File(celosWorkDir, DB_DIR_CELOS_PATH);

        workflowDir.mkdirs();
        defaultsDir.mkdirs();
        dbDir.mkdirs();

        ciContext.getTestContext().setCelosWorkflowDir(workflowDir);
        ciContext.getTestContext().setCelosDefaultsDir(defaultsDir);
        ciContext.getTestContext().setCelosDbDir(dbDir);

        JScpWorker worker = new JScpWorker(ciContext.getUserName(), ciContext.getTarget().getScpSecuritySettings());
        FileObject remoteDefaultsFile = worker.getFileObjectByUri(ciContext.getTarget().getDefaultsFile());
        if (remoteDefaultsFile.exists()) {
            FileObject localDefaultsFile = worker.getFileObjectByUri(new File(defaultsDir, remoteDefaultsFile.getName().getBaseName()).toString());
            localDefaultsFile.copyFrom(remoteDefaultsFile, Selectors.SELECT_SELF);
        }
    }

    private static void runServer(CelosCiContext ciContext) throws Exception {
        final CelosServer celosServer = new CelosServer();

        try {

            Integer port = celosServer.startServer(
                    ImmutableMap.of(HDFS_PREFIX, ciContext.getTestContext().getHdfsPrefix()),
                    ciContext.getTestContext().getCelosWorkflowDir().toString(),
                    ciContext.getTestContext().getCelosDefaultsDir().toString(),
                    ciContext.getTestContext().getCelosDbDir().toString()
            );

            ciContext.getTestContext().setCelosPort(port);


            System.out.println("Deploying workflow " + ciContext.getWorkflowName());
            deploy(ciContext);

            new CelosSchedulerRunner(ciContext).runCelosScheduler();
        } finally {
            System.out.println("Job is finished");
            celosServer.stopServer();
        }
    }


    private static void deploy(CelosCiContext config) throws Exception {

        WorkflowFileDeployer wfDeployer = new WorkflowFileDeployer(config);
        HdfsDeployer hdfsDeployer = new HdfsDeployer(config);
        if (config.getMode() == CelosCiContext.Mode.DEPLOY || config.getMode() == CelosCiContext.Mode.TEST) {
            wfDeployer.deploy();
            hdfsDeployer.deploy();
        } else if (config.getMode() == CelosCiContext.Mode.UNDEPLOY) {
            wfDeployer.undeploy();
            hdfsDeployer.undeploy();
        }
    }


}