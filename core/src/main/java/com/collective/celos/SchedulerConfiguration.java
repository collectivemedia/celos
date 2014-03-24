package com.collective.celos;

import java.io.File;
import java.io.IOException;

/**
 * Reads configuration and database from filesystem at well-known paths.
 */
public class SchedulerConfiguration {

    private static final String WORKFLOW_CONFIGURATION_PATH = "/etc/celos/workflows";
    private static final String DEFAULTS_CONFIGURATION_PATH = "/etc/celos/defaults";
    private static final String STATE_DATABASE_PATH = "/var/lib/celos/db";

    public SchedulerConfiguration() {        
    }
    
    public Scheduler makeDefaultScheduler() throws Exception {
        File configDir = new File(WORKFLOW_CONFIGURATION_PATH);
        File defaultsDir = new File(DEFAULTS_CONFIGURATION_PATH);
        WorkflowConfiguration config =
                new WorkflowConfigurationParser(defaultsDir).parseConfiguration(configDir).getWorkflowConfiguration();
        StateDatabase db = makeDefaultStateDatabase();
        int slidingWindowHours = 24 * 7;
        return new Scheduler(config, db, slidingWindowHours);
    }

    public StateDatabase makeDefaultStateDatabase() throws IOException {
        File dbDir = new File(STATE_DATABASE_PATH);
        return new FileSystemStateDatabase(dbDir);
    }

}
