package com.collective.celos.server;

import com.collective.celos.Util;

import java.io.File;

public class ServerCommandLine {

    private final File workflowsDir;
    private final File defaultsDir;
    private final File stateDatabase;
    private final File uiDir;
    private final File logDir;
    private final int port;
    private final String version;


    public ServerCommandLine(String workflowsDir, String defaultsDir,
                             String stateDatabase, String uiDir, String logDir, String version, int port) {
        assert version != null;
        this.version = version;
        assert workflowsDir != null;
        this.workflowsDir = new File(Util.requireNonNull(workflowsDir));
        assert defaultsDir != null;
        this.defaultsDir = new File(Util.requireNonNull(defaultsDir));
        assert stateDatabase != null;
        this.stateDatabase = new File(stateDatabase);
        assert uiDir != null;
        this.uiDir = new File(uiDir);
        this.port = port;
        assert logDir != null;
        this.logDir = new File(logDir);
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

    public int getPort() {
        return port;
    }

    public File getLogDir() {
        return logDir;
    }


    public String getVersion() {
        return version;
    }
}
