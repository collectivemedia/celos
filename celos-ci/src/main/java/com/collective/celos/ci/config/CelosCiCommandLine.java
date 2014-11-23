package com.collective.celos.ci.config;

import com.collective.celos.ci.config.deploy.CelosCiContext;

import java.io.File;
import java.net.URI;

/**
 * Created by akonopko on 9/30/14.
 */
public class CelosCiCommandLine {

    private final URI targetUri;
    private final CelosCiContext.Mode mode;
    private final File deployDir;
    private final String workflowName;
    private final String userName;

    public CelosCiCommandLine(String targetUri, String mode, String deployDir, String workflowName, String userName) {
        this.userName = userName;
        this.targetUri = URI.create(targetUri);
        this.mode = CelosCiContext.Mode.valueOf(mode);
        this.deployDir = new File(deployDir);
        this.workflowName = workflowName;
    }

    public URI getTargetUri() {
        return targetUri;
    }

    public CelosCiContext.Mode getMode() {
        return mode;
    }

    public File getDeployDir() {
        return deployDir;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public String getUserName() {
        return userName;
    }
}
