/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos.server;

import com.collective.celos.Util;
import com.collective.celos.database.StateDatabase;

import java.io.File;

/**
 * Command-line options for the scheduler server.
 */
public class ServerCommandLine {

    private final File workflowsDir;
    private final File defaultsDir;
    private final StateDatabase database;
    private final File logDir;
    private final int port;
    private final int autoSchedule;
    private final String zookeeperUri;

    public ServerCommandLine(String workflowsDir, String defaultsDir, StateDatabase db, String logDir, int port, int autoSchedule, String zookeeperUri) {
        this.autoSchedule = autoSchedule;
        this.zookeeperUri = zookeeperUri;
        this.workflowsDir = new File(Util.requireNonNull(workflowsDir));
        this.defaultsDir = new File(Util.requireNonNull(defaultsDir));
        this.database = Util.requireNonNull(db);
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

    public int getPort() {
        return port;
    }

    public File getLogDir() {
        return logDir;
    }

    public StateDatabase getDatabase() {
        return database;
    }

    public String getZookeeperUri() {
        return zookeeperUri;
    }
}
