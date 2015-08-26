package com.collective.celos.server;

import com.collective.celos.Util;

import java.io.File;

/**
 * Created by akonopko on 9/30/14.
 */
public class ServerCommandLine {

    private final File workflowsDir;
    private final File defaultsDir;
    private final File stateDatabase;
    private final File logDir;
    private final int port;
    private final boolean autoSchedule;

    public ServerCommandLine(String workflowsDir, String defaultsDir, String stateDatabase, String logDir, int port, boolean autoSchedule) {
        this.autoSchedule = autoSchedule;
        this.workflowsDir = new File(Util.requireNonNull(workflowsDir));
        this.defaultsDir = new File(Util.requireNonNull(defaultsDir));
        this.stateDatabase = new File(Util.requireNonNull(stateDatabase));
        this.port = port;
        this.logDir = new File(Util.requireNonNull(logDir));
    }

    public boolean isAutoSchedule() {
        return autoSchedule;
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

    public int getPort() {
        return port;
    }

    public File getLogDir() {
        return logDir;
    }
}
