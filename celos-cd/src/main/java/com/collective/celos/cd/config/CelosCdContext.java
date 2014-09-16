package com.collective.celos.cd.config;

public class CelosCdContext {

    public static enum Mode {
        DEPLOY, UNDEPLOY
    }

    private final CelosCdTarget target;
    private final String userName;
    private final Mode mode;
    private final String pathToWorkflow;
    private final String workflowName;
    private final String celosWorkflowsDirUri;

    public CelosCdContext(CelosCdTarget target,
                          String userName,
                          Mode mode,
                          String pathToWorkflow,
                          String workflowName,
                          String workflowsCelosDirUri) {
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
        this.pathToWorkflow = pathToWorkflow;
        this.workflowName = workflowName;
        this.celosWorkflowsDirUri = workflowsCelosDirUri;
    }

    public String getUserName() {
        return userName;
    }

    public Mode getMode() {
        return mode;
    }

    public String getPathToWorkflow() {
        return pathToWorkflow;
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


}
