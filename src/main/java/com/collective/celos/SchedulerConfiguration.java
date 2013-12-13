package com.collective.celos;

import java.io.File;

/**
 * Reads configuration and database from filesystem at well-known paths.
 */
public class SchedulerConfiguration {

    private static final String CONFIG_PATH = "/etc/celos/workflows";
    private static final String DB_PATH = "/var/lib/celos/db";

    public SchedulerConfiguration() {        
    }
    
    public Scheduler makeDefaultScheduler() throws Exception {
        File configFile = new File(CONFIG_PATH);
        File dbFile = new File(DB_PATH);
        WorkflowConfiguration config =
                new WorkflowConfigurationParser().parseConfiguration(configFile);
        StateDatabase db = new FileSystemStateDatabase(dbFile);
        int slidingWindowHours = 24 * 7;
        return new Scheduler(config, db, slidingWindowHours);
    }

}
