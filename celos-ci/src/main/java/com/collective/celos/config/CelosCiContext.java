package com.collective.celos.config;


import org.apache.hadoop.fs.FileSystem;

import java.io.File;

public class CelosCiContext {

    public static enum Mode {
        DEPLOY, UNDEPLOY, TEST
    }

    private final CelosCiTarget target;
    private final String userName;
    private final Mode mode;
    private final File deployDir;
    private final String workflowName;
    private final FileSystem fileSystem;
    private final TestContext testContext;

    public CelosCiContext(CelosCiTarget target,
                          String userName,
                          Mode mode,
                          File deployDir,
                          String workflowName,
                          TestContext testContext) throws Exception {
        this.target = target;
        this.userName = userName;
        this.mode = mode;
        this.deployDir = deployDir;
        this.workflowName = workflowName;
        this.fileSystem = new HdfsConfig(userName, target).getFileSystem();
        this.testContext = testContext;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public String getUserName() {
        return userName;
    }

    public Mode getMode() {
        return mode;
    }

    public File getDeployDir() {
        return deployDir;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public CelosCiTarget getTarget() {
        return target;
    }

    public TestContext getTestContext() {
        return testContext;
    }

    public String getHdfsPrefix() {
        return testContext.getHdfsPrefix() == null ? "" : testContext.getHdfsPrefix();
    }

}
