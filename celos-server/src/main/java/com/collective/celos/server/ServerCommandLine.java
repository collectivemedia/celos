package com.collective.celos.server;

import com.collective.celos.Util;

import java.io.File;

/**
 * Command-line options for the scheduler server.
 */
public class ServerCommandLine {

    private final File workflowsDir;
    private final File defaultsDir;
    private final File stateDatabase;
    private final File logDir;
    private final int port;
    private final int autoSchedule;

    public ServerCommandLine(String workflowsDir, String defaultsDir, String stateDatabase, String logDir, int port, int autoSchedule) {
        this.autoSchedule = autoSchedule;
        this.workflowsDir = new File(Util.requireNonNull(workflowsDir));
        this.defaultsDir = new File(Util.requireNonNull(defaultsDir));
        this.stateDatabase = new File(Util.requireNonNull(stateDatabase));
        this.port = port;
        this.logDir = new File(Util.requireNonNull(logDir));
    }

    public int getAutoSchedule() {
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
