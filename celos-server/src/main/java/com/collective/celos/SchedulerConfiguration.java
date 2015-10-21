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
    private final File stateDatabasePath;
    private final Map<String, String> additionalVars;
    private final int swarmSize;
    private final int celosNumber;

    public SchedulerConfiguration(File workflowConfigurationPath, File defaultsConfigurationPath, File stateDatabasePath, Map<String, String> additionalVars, int swarmSize, int celosNumber) {
        this.workflowConfigurationPath = workflowConfigurationPath;
        this.defaultsConfigurationPath = defaultsConfigurationPath;
        this.stateDatabasePath = stateDatabasePath;
        this.additionalVars = additionalVars;
        this.swarmSize = swarmSize;
        this.celosNumber = celosNumber;
    }

    public Scheduler makeDefaultScheduler() throws Exception {
        WorkflowConfiguration config = getWorkflowConfigurationParser().getWorkflowConfiguration();
        StateDatabase db = makeDefaultStateDatabase();
        int slidingWindowHours = 24 * SLIDING_WINDOW_DAYS;
        return new Scheduler(config, db, slidingWindowHours, swarmSize, celosNumber);
    }

    private WorkflowConfigurationParser getWorkflowConfigurationParser() throws Exception {
        return new WorkflowConfigurationParser(defaultsConfigurationPath, additionalVars).parseConfiguration(workflowConfigurationPath);
    }

    private StateDatabase makeDefaultStateDatabase() throws IOException {
        return new FileSystemStateDatabase(stateDatabasePath);
    }

}
