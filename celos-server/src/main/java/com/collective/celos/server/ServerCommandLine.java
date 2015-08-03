package com.collective.celos.server;

import java.io.File;

/**
 * Created by akonopko on 9/30/14.
 */
public class ServerCommandLine {

    private final File workflowsDir;
    private final File defaultsDir;
    private final File stateDatabase;
    private final File uiDir;
    private final Integer port;

    public ServerCommandLine(String workflowsDir, String defaultsDir, String stateDatabase, String uiDir, Integer port) {
        this.workflowsDir = new File(workflowsDir);
        this.defaultsDir = new File(defaultsDir);
        this.stateDatabase = new File(stateDatabase);
        this.uiDir = new File(uiDir);
        this.port = port;
    }

    public File getWorkflowsDir() {
        return workflowsDir;
    }

    public File getDefaultsDir() {
        return defaultsDir;
    }

    public File getStateDatabase() {
        return stateDatabase;
    }

    public File getUiDir() {
        return uiDir;
    }

    public Integer getPort() {
        return port;
    }
}
