package com.collective.celos;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Reads configuration and database from filesystem at well-known paths.
 */
public class SchedulerConfiguration {

    private static final Logger LOGGER = Logger.getLogger(SchedulerConfiguration.class);

    private static final String WORKFLOW_CONFIGURATION_PATH = "/etc/celos/workflows";
    private static final String DEFAULTS_CONFIGURATION_PATH = "/etc/celos/defaults";
    private static final String STATE_DATABASE_PATH = "/var/lib/celos/db";

    public SchedulerConfiguration() {        
    }
    
    public Scheduler makeDefaultScheduler() throws Exception {
        WorkflowConfiguration config = getWorkflowConfigurationParser().getWorkflowConfiguration();
        StateDatabase db = makeDefaultStateDatabase();
        int slidingWindowHours = 24 * 7;
        return new Scheduler(config, db, slidingWindowHours);
    }

    public String getWorkflowConfigurationFileContents(String workflowId) throws Exception {
        WorkflowConfigurationParser parser = getWorkflowConfigurationParser();

        String filePath = parser.getWorkflowConfiguration().getWorkflowJSFileName(new WorkflowID(workflowId));

        if (filePath == null) {
            return null;
        }

        return FileUtils.readFileToString(new File(filePath));
    }

    private WorkflowConfigurationParser getWorkflowConfigurationParser() throws Exception {
        File configDir = new File(WORKFLOW_CONFIGURATION_PATH);
        File defaultsDir = new File(DEFAULTS_CONFIGURATION_PATH);
        return new WorkflowConfigurationParser(defaultsDir).parseConfiguration(configDir);
    }

    public StateDatabase makeDefaultStateDatabase() throws IOException {
        File dbDir = new File(STATE_DATABASE_PATH);
        return new FileSystemStateDatabase(dbDir);
    }

}
