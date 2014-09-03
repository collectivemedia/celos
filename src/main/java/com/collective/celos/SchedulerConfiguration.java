package com.collective.celos;

import com.collective.celos.server.ServerConfig;

import java.io.File;
import java.io.IOException;

/**
 * Reads configuration and database from filesystem at well-known paths.
 */
public class SchedulerConfiguration {

    private ServerConfig serverConfig;

    public SchedulerConfiguration(ServerConfig serverConfig) {
    }
    
    public Scheduler makeDefaultScheduler() throws Exception {
        WorkflowConfiguration config = getWorkflowConfigurationParser().getWorkflowConfiguration();
        StateDatabase db = makeDefaultStateDatabase();
        int slidingWindowHours = 24 * 7;
        return new Scheduler(config, db, slidingWindowHours);
    }

    private WorkflowConfigurationParser getWorkflowConfigurationParser() throws Exception {
        File configDir = new File(serverConfig.getWorkflowConfigurationPath());
        File defaultsDir = new File(serverConfig.getDefaultsConfigurationPath());
        return new WorkflowConfigurationParser(defaultsDir).parseConfiguration(configDir);
    }

    private StateDatabase makeDefaultStateDatabase() throws IOException {
        File dbDir = new File(serverConfig.getStateDatabasePath());
        return new FileSystemStateDatabase(dbDir);
    }

}
