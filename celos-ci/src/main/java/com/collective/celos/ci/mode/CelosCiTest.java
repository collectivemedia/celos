package com.collective.celos.ci.mode;

import com.collective.celos.ci.CelosCi;
import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.config.testing.TestContext;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Created by akonopko on 10/1/14.
 */
public class CelosCiTest extends CelosCi {

    private static final String HDFS_PREFIX_PATTERN = "/user/%s/test/%s/%s";

    private final CelosCiContext ciContext;
    private final TestContext testContext;

    public CelosCiTest(CelosCiCommandLine commandLine) throws Exception {

        CelosCiTargetParser parser = new CelosCiTargetParser(commandLine.getUserName());
        CelosCiTarget target = parser.parse(commandLine.getTargetUri());

        File celosTempDir = Files.createTempDirectory("celos").toFile();
        System.out.println("Temp dir for Celos is " + celosTempDir.getAbsolutePath().toString());
        String hdfsPrefix = String.format(HDFS_PREFIX_PATTERN, commandLine.getUserName(), commandLine.getWorkflowName(), UUID.randomUUID().toString());
        System.out.println("HDFS prefix is: " + hdfsPrefix);

        this.testContext = new TestContext(celosTempDir, hdfsPrefix, commandLine.getTestCasesDir());
        URI substitutedCelosWorkflowDir = testContext.getCelosWorkflowDir().toURI();
        CelosCiTarget testTarget = new CelosCiTarget(target.getScpSecuritySettings(), target.getPathToHdfsSite(), target.getPathToCoreSite(), substitutedCelosWorkflowDir, target.getDefaultsFile());
        this.ciContext = new CelosCiContext(testTarget, commandLine.getUserName(), commandLine.getMode(), commandLine.getDeployDir(), commandLine.getWorkflowName(), hdfsPrefix);
    }

    @Override
    public void start() throws Exception {
        //TODO:
    }

    public CelosCiContext getCiContext() {
        return ciContext;
    }

    public TestContext getTestContext() {
        return testContext;
    }

}
