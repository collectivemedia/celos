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
package com.collective.celos;

import com.collective.celos.database.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Reads configuration and database from filesystem at well-known paths.
 */
public class SchedulerConfiguration {

    public static final int SLIDING_WINDOW_DAYS = 7;

    private final File workflowConfigurationPath;
    private final File defaultsConfigurationPath;
    private final StateDatabase db;
    private final Map<String, String> additionalVars;

    public SchedulerConfiguration(File workflowConfigurationPath, File defaultsConfigurationPath, StateDatabase.Config config, Map<String, String> additionalVars) throws IOException {
        this.workflowConfigurationPath = workflowConfigurationPath;
        this.defaultsConfigurationPath = defaultsConfigurationPath;
        this.additionalVars = additionalVars;
        this.db = createStateDatabase(config);
    }

    public Scheduler makeDefaultScheduler() throws Exception {
        WorkflowConfiguration config;
        try(StateDatabaseConnection conn = db.openConnection()) {
            config = getWorkflowConfigurationParser(conn).getWorkflowConfiguration();
        }
        int slidingWindowHours = 24 * SLIDING_WINDOW_DAYS;
        return new Scheduler(config, slidingWindowHours);
    }

    private WorkflowConfigurationParser getWorkflowConfigurationParser(StateDatabaseConnection conn) throws Exception {
        return new WorkflowConfigurationParser(defaultsConfigurationPath, additionalVars).parseConfiguration(workflowConfigurationPath, conn);
    }

    public StateDatabase getStateDatabase() {
        return db;
    }

    private StateDatabase createStateDatabase(StateDatabase.Config config) throws IOException {
        switch (config.getDatabaseType()) {
            case JDBC:
                return new JDBCStateDatabase((JDBCStateDatabase.Config) config);
            case FILESYSTEM:
                return new FileSystemStateDatabase((FileSystemStateDatabase.Config) config);
            default:
                throw new IllegalArgumentException("Unsupported Celos DB type: " + config.getDatabaseType());
        }
    }

}
