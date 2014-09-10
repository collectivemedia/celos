package com.collective.celos;

import java.io.File;
import java.io.IOException;

/**
 * Reads configuration and database from filesystem at well-known paths.
 */
public class SchedulerConfiguration {

    private String workflowConfigurationPath;
    private String defaultsConfigurationPath;
    private String stateDatabasePath;

    public SchedulerConfiguration(String workflowConfigurationPath, String defaultsConfigurationPath, String stateDatabasePath) {
        this.workflowConfigurationPath = workflowConfigurationPath;
        this.defaultsConfigurationPath = defaultsConfigurationPath;
        this.stateDatabasePath = stateDatabasePath;
    }
    
    public Scheduler makeDefaultScheduler() throws Exception {
        WorkflowConfiguration config = getWorkflowConfigurationParser().getWorkflowConfiguration();
        StateDatabase db = makeDefaultStateDatabase();
        int slidingWindowHours = 24 * 7;
        return new Scheduler(config, db, slidingWindowHours);
    }

    private WorkflowConfigurationParser getWorkflowConfigurationParser() throws Exception {
        File configDir = new File(workflowConfigurationPath);
        File defaultsDir = new File(defaultsConfigurationPath);
        return new WorkflowConfigurationParser(defaultsDir).parseConfiguration(configDir);
    }

    private StateDatabase makeDefaultStateDatabase() throws IOException {
        File dbDir = new File(stateDatabasePath);
        return new FileSystemStateDatabase(dbDir);
    }

}
