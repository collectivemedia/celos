package com.collective.celos.cd.config;


import org.apache.hadoop.fs.FileSystem;

public class CelosCdContext {

    public static enum Mode {
        DEPLOY, UNDEPLOY
    }

    private final CelosCdTarget target;
    private final String userName;
    private final Mode mode;
    private final String deployDir;
    private final String workflowName;
    private final String celosWorkflowsDirUri;
    private final String hdfsPrefix;
    private final FileSystem fileSystem;

    public CelosCdContext(CelosCdTarget target,
                          String userName,
                          Mode mode,
                          String deployDir,
                          String workflowName,
                          String celosWworkflowsDirUri,
                          String hdfsPrefix) throws Exception {
        this.target = target;
        if (userName == null) {
            this.userName = System.getProperty("user.name");
        } else {
            this.userName = userName;
        }
        if (mode == null) {
            this.mode = mode;
        } else {
            this.mode = Mode.DEPLOY;
        }
        this.deployDir = deployDir;
        this.workflowName = workflowName;
        this.celosWorkflowsDirUri = celosWworkflowsDirUri;
        this.hdfsPrefix = hdfsPrefix;
        this.fileSystem = new HdfsConfig(userName, target).getFileSystem();
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

    public String getDeployDir() {
        return deployDir;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public CelosCdTarget getTarget() {
        return target;
    }

    public String getCelosWorkflowsDirUri() {
        return celosWorkflowsDirUri;
    }

    public String getHdfsPrefix() {
        return hdfsPrefix;
    }
}
