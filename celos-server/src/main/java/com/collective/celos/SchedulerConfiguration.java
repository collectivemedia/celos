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

    public SchedulerConfiguration(File workflowConfigurationPath, File defaultsConfigurationPath, File stateDatabasePath, Map<String, String> additionalVars) {
        this.workflowConfigurationPath = workflowConfigurationPath;
        this.defaultsConfigurationPath = defaultsConfigurationPath;
        this.stateDatabasePath = stateDatabasePath;
        this.additionalVars = additionalVars;
    }

    public Scheduler makeDefaultScheduler() throws Exception {
        WorkflowConfiguration config = getWorkflowConfigurationParser().getWorkflowConfiguration();
        StateDatabase db = makeDefaultStateDatabase();
        int slidingWindowHours = 24 * SLIDING_WINDOW_DAYS;
        return new Scheduler(config, db, slidingWindowHours);
    }

    private WorkflowConfigurationParser getWorkflowConfigurationParser() throws Exception {
        return new WorkflowConfigurationParser(defaultsConfigurationPath, additionalVars).parseConfiguration(workflowConfigurationPath);
    }

    private StateDatabase makeDefaultStateDatabase() throws IOException {
        return new FileSystemStateDatabase(stateDatabasePath);
    }

}
