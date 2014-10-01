package com.collective.celos.ci;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.ContextParser;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.config.testing.TestContext;
import com.collective.celos.ci.deploy.HdfsDeployer;
import com.collective.celos.ci.deploy.WorkflowFileDeployer;
import com.collective.celos.ci.testing.CelosCiIntegrationTestRunner;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class CelosCi {

    public static void main(String... args) throws Exception {

        ContextParser contextParser = new ContextParser();
        CelosCiCommandLine commandLine = contextParser.parse(args);

        new CelosCi().start(commandLine);
    }

    private static final String HDFS_PREFIX_PATTERN = "/user/%s/test/%s/%s";


    public void start(CelosCiCommandLine commandLine) throws Exception {
        CelosCiTargetParser parser = new CelosCiTargetParser(commandLine.getUserName());
        CelosCiTarget target = parser.parse(commandLine.getTargetUri());

        if (commandLine.getMode() == CelosCiContext.Mode.TEST) {
            onTestMode(commandLine, target);
        } else if (commandLine.getMode() == CelosCiContext.Mode.DEPLOY) {
            onDeployMode(commandLine, target);
        } else if (commandLine.getMode() == CelosCiContext.Mode.UNDEPLOY) {
            onUndeployMode(commandLine, target);
        }

    }

    private void onUndeployMode(CelosCiCommandLine commandLine, CelosCiTarget target) throws Exception {
        CelosCiContext ciContext = new CelosCiContext(target, commandLine.getUserName(), commandLine.getMode(), commandLine.getDeployDir(), commandLine.getWorkflowName(), "");
        WorkflowFileDeployer wfDeployer = new WorkflowFileDeployer(ciContext);
        HdfsDeployer hdfsDeployer = new HdfsDeployer(ciContext);
        wfDeployer.undeploy();
        hdfsDeployer.undeploy();
    }

    private void onDeployMode(CelosCiCommandLine commandLine, CelosCiTarget target) throws Exception {
        CelosCiContext ciContext = new CelosCiContext(target, commandLine.getUserName(), commandLine.getMode(), commandLine.getDeployDir(), commandLine.getWorkflowName(), "");
        WorkflowFileDeployer wfDeployer = new WorkflowFileDeployer(ciContext);
        HdfsDeployer hdfsDeployer = new HdfsDeployer(ciContext);
        wfDeployer.deploy();
        hdfsDeployer.deploy();
    }

    private void onTestMode(CelosCiCommandLine commandLine, CelosCiTarget target) throws Exception {
        Path tempDir = Files.createTempDirectory("celos");
        File celosWorkDir = tempDir.toFile();
        System.out.println("Temp dir for Celos is " + tempDir.toAbsolutePath().toString());
        String hdfsPrefix = String.format(HDFS_PREFIX_PATTERN, commandLine.getUserName(), commandLine.getWorkflowName(), UUID.randomUUID().toString());
        System.out.println("HDFS prefix is: " + hdfsPrefix);

        TestContext testContext = new TestContext(celosWorkDir, hdfsPrefix, commandLine.getTestCasesDir());
        URI substitutedCelosWorkflowDir = testContext.getCelosWorkflowDir().toURI();
        CelosCiTarget testTarget = new CelosCiTarget(target.getScpSecuritySettings(), target.getPathToHdfsSite(), target.getPathToCoreSite(), substitutedCelosWorkflowDir, target.getDefaultsFile());
        CelosCiContext celosCiContext = new CelosCiContext(testTarget, commandLine.getUserName(), commandLine.getMode(), commandLine.getDeployDir(), commandLine.getWorkflowName(), hdfsPrefix);

        new CelosCiIntegrationTestRunner(celosCiContext, testContext).runTests();
    }
}
