package com.collective.celos.config;

public class Config {

    public static enum Mode {
        DEPLOY, UNDEPLOY
    }

    public static final String DEFAULT_SECURITY_SETTINGS = "gssapi-with-mic,publickey,keyboard-interactive,password";

    private String scpSecuritySettings = DEFAULT_SECURITY_SETTINGS;
    private String userName = System.getProperty("user.name");
    private Mode mode = Mode.DEPLOY;

    private String pathToHdfsSite;
    private String pathToCoreSite;
    private String pathToWorkflow;
    private String workflowName;
    private String targetFile;
    private String celosWorkflowsDirUri;
    private String celosDbDirUri;
    private String celosDefaultsDirUri;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCelosWorkflowsDirUri() {
        return celosWorkflowsDirUri;
    }

    public void setCelosWorkflowsDirUri(String celosWorkflowsDirUri) {
        this.celosWorkflowsDirUri = celosWorkflowsDirUri;
    }

    public String getPathToWorkflow() {
        return pathToWorkflow;
    }

    public void setPathToWorkflow(String pathToWorkflow) {
        this.pathToWorkflow = pathToWorkflow;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getScpSecuritySettings() {
        return scpSecuritySettings;
    }

    public void setScpSecuritySettings(String scpSecuritySettings) {
        this.scpSecuritySettings = scpSecuritySettings;
    }

    public String getPathToHdfsSite() {
        return pathToHdfsSite;
    }

    public void setPathToHdfsSite(String pathToHdfsSite) {
        this.pathToHdfsSite = pathToHdfsSite;
    }

    public String getPathToCoreSite() {
        return pathToCoreSite;
    }

    public void setPathToCoreSite(String pathToCoreSite) {
        this.pathToCoreSite = pathToCoreSite;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }

    public String getCelosDbDirUri() {
        return celosDbDirUri;
    }

    public void setCelosDbDirUri(String celosDbDirUri) {
        this.celosDbDirUri = celosDbDirUri;
    }

    public String getCelosDefaultsDirUri() {
        return celosDefaultsDirUri;
    }

    public void setCelosDefaultsDirUri(String celosDefaultsDirUri) {
        this.celosDefaultsDirUri = celosDefaultsDirUri;
    }

    public void validate() {
        if (getCelosWorkflowsDirUri() == null) {
            throw new IllegalStateException("Celos workflow dir is not set");
        }
        if (getCelosDefaultsDirUri() == null) {
            throw new IllegalStateException("Celos defaults dir is not set");
        }
        if (getCelosDbDirUri() == null) {
            throw new IllegalStateException("Celos db dir is not set");
        }
        if (getWorkflowName() == null) {
            throw new IllegalStateException("Workflow name is not set");
        }
        if (getPathToWorkflow() == null) {
            throw new IllegalStateException("Workflow dir is not set");
        }
    }

}
