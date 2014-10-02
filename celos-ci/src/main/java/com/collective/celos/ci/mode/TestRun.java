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
public class TestRun {

    private static final String HDFS_PREFIX_PATTERN = "/user/%s/test/%s/%s";

    private final CelosCiContext ciContext;
    private final TestContext testContext;
    private final String name;

    public TestRun(CelosCiTarget target, String username, String workflowName, File deployDir, File testCaseDir) throws Exception {

        File celosTempDir = Files.createTempDirectory("celos").toFile();
        this.name = testCaseDir.getName();

        System.out.println("Test case " + name + ": temp dir for Celos is " + celosTempDir.getAbsolutePath().toString());
        String hdfsPrefix = String.format(HDFS_PREFIX_PATTERN, username, workflowName, UUID.randomUUID().toString());
        System.out.println("Test case " + name + ": HDFS prefix is: " + hdfsPrefix);

        this.testContext = new TestContext(celosTempDir, hdfsPrefix, testCaseDir);
        URI substitutedCelosWorkflowDir = testContext.getCelosWorkflowDir().toURI();
        CelosCiTarget testTarget = new CelosCiTarget(target.getPathToHdfsSite(), target.getPathToCoreSite(), substitutedCelosWorkflowDir, target.getDefaultsFile());
        this.ciContext = new CelosCiContext(testTarget, username, CelosCiContext.Mode.TEST, deployDir, workflowName, hdfsPrefix);
    }

    public void start() throws Exception {
        //TODO:
    }

    public String getName() {
        return name;
    }

    public CelosCiContext getCiContext() {
        return ciContext;
    }

    public TestContext getTestContext() {
        return testContext;
    }

}
