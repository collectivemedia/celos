package com.collective.celos;

import java.io.File;
import java.util.Map;

/**
 * Reads configuration and database from filesystem at well-known paths.
 */
public class SchedulerConfiguration {

    private SchedulerConfiguration() {
        assert false;
    }


    public static final int DEFAULT_SLIDING_WINDOW_DAYS = 7;


    public static Scheduler makeDefaultScheduler(File workflowConfigurationPath, File defaultsConfigurationPath,
                                          File stateDatabasePath,
                                          Map<String, String> additionalVars) throws Exception {
        WorkflowConfiguration config = new WorkflowConfigurationParser(defaultsConfigurationPath, additionalVars).
                parseConfiguration(workflowConfigurationPath).
                getWorkflowConfiguration();
        StateDatabase db = new FileSystemStateDatabase(stateDatabasePath);
        int slidingWindowHours = 24 * DEFAULT_SLIDING_WINDOW_DAYS;
        return new Scheduler(config, db, slidingWindowHours);
    }


}
