package com.collective.celos;

import java.io.File;
import java.util.Map;

/**
 * Reads configuration and database from filesystem at well-known paths.
 */
public class SchedulerConfiguration {


    private final File workflowConfigurationPath;
    private final File defaultsConfigurationPath;
    private final File stateDatabasePath;
    private final File rerunDatabasePath;
    private final File uiDir;
    private final Map<String, String> additionalVars;

    public SchedulerConfiguration(File workflowConfigurationPath, File defaultsConfigurationPath,
                                  File stateDatabasePath,
                                  File rerunDatabasePath,
                                  File uiDir, Map<String, String> additionalVars) {
        this.workflowConfigurationPath = workflowConfigurationPath;
        this.defaultsConfigurationPath = defaultsConfigurationPath;
        this.stateDatabasePath = stateDatabasePath;
        this.rerunDatabasePath = rerunDatabasePath;
        this.uiDir = uiDir;
        this.additionalVars = additionalVars;
    }


}
