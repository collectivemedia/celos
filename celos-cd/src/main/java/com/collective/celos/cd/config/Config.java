package com.collective.celos.cd.config;

public class Config {

    public static enum Mode {
        DEPLOY, UNDEPLOY
    }

    public static final String DEFAULT_SECURITY_SETTINGS = "gssapi-with-mic,publickey,keyboard-interactive,password";

    private final String scpSecuritySettings;
    private final String userName;
    private final Mode mode;
    private final String pathToHdfsSite;
    private final String pathToCoreSite;
    private final String pathToWorkflow;
    private final String workflowName;
    private final String celosWorkflowsDirUri;

    public Config(String scpSecuritySettings,
                  String userName,
                  Mode mode,
                  String pathToHdfsSite,
                  String pathToCoreSite,
                  String pathToWorkflow,
                  String workflowName,
                  String celosWorkflowsDirUri) {
        if (scpSecuritySettings == null) {
            this.scpSecuritySettings = DEFAULT_SECURITY_SETTINGS;
        } else {
            this.scpSecuritySettings = scpSecuritySettings;
        }
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
        this.pathToHdfsSite = pathToHdfsSite;
        this.pathToCoreSite = pathToCoreSite;
        this.pathToWorkflow = pathToWorkflow;
        this.workflowName = workflowName;
        this.celosWorkflowsDirUri = celosWorkflowsDirUri;
    }

    public String getScpSecuritySettings() {
        return scpSecuritySettings;
    }

    public String getUserName() {
        return userName;
    }

    public Mode getMode() {
        return mode;
    }

    public String getPathToHdfsSite() {
        return pathToHdfsSite;
    }

    public String getPathToCoreSite() {
        return pathToCoreSite;
    }

    public String getPathToWorkflow() {
        return pathToWorkflow;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public String getCelosWorkflowsDirUri() {
        return celosWorkflowsDirUri;
    }

}
