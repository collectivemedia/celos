package com.collective.celos;

import java.io.File;

/**
 * Reads configuration and database from filesystem at well-known paths.
 */
public class SchedulerConfiguration {

    private static final String WORKFLOW_CONFIGURATION_PATH = "/etc/celos/workflows";
    private static final String STATE_DATABASE_PATH = "/var/lib/celos/db";

    public SchedulerConfiguration() {        
    }
    
    public Scheduler makeDefaultScheduler() throws Exception {
        File configDir = new File(WORKFLOW_CONFIGURATION_PATH);
        File dbDir = new File(STATE_DATABASE_PATH);
        WorkflowConfiguration config =
                new WorkflowConfigurationParser().parseConfiguration(configDir);
        StateDatabase db = new FileSystemStateDatabase(dbDir);
        int slidingWindowHours = 24 * 7;
        return new Scheduler(config, db, slidingWindowHours);
    }

}
