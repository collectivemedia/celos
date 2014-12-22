package com.collective.celos;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Reads configuration and database from filesystem at well-known paths.
 */
public class SchedulerConfiguration {

    public static final int SLIDING_WINDOW_DAYS = 7;

    private final String workflowConfigurationPath;
    private final String defaultsConfigurationPath;
    private final String stateDatabasePath;
    private final Map<String, String> additionalVars;

    public SchedulerConfiguration(String workflowConfigurationPath, String defaultsConfigurationPath, String stateDatabasePath, Map<String, String> additionalVars) {
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
        File configDir = new File(workflowConfigurationPath);
        File defaultsDir = new File(defaultsConfigurationPath);
        return new WorkflowConfigurationParser(defaultsDir, additionalVars).parseConfiguration(configDir);
    }

    private StateDatabase makeDefaultStateDatabase() throws IOException {
        File dbDir = new File(stateDatabasePath);
        return new FileSystemStateDatabase(dbDir);
    }

}
