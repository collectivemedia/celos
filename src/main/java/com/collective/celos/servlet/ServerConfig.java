package com.collective.celos.servlet;

import java.util.Map;

/**
 * Created by akonopko on 8/29/14.
 */
public class ServerConfig {

    public static final String WORKFLOW_CONFIGURATION_PATH = "/home/akonopko/celos2/ci/workflows";
    public static final String DEFAULTS_CONFIGURATION_PATH = "/home/akonopko/celos2/ci/defaults";
    public static final String STATE_DATABASE_PATH = "/home/akonopko/celos2/ci/db";

//    private static final String WORKFLOW_CONFIGURATION_PATH = "/etc/celos/workflows";
//    private static final String DEFAULTS_CONFIGURATION_PATH = "/etc/celos/defaults";
//    private static final String STATE_DATABASE_PATH = "/var/lib/celos/db";


    private String workflowConfigurationPath;
    private String defaultsConfigurationPath;
    private String stateDatabasePath;
    private Map<String, String> jsVariables;

    public void setWorkflowConfigurationPath(String workflowConfigurationPath) {
        this.workflowConfigurationPath = workflowConfigurationPath;
    }

    public void setDefaultsConfigurationPath(String defaultsConfigurationPath) {
        this.defaultsConfigurationPath = defaultsConfigurationPath;
    }

    public void setStateDatabasePath(String stateDatabasePath) {
        this.stateDatabasePath = stateDatabasePath;
    }

    public void setJsVariables(Map<String, String> jsVariables) {
        this.jsVariables = jsVariables;
    }

    public String getWorkflowConfigurationPath() {
        return workflowConfigurationPath;
    }

    public String getDefaultsConfigurationPath() {
        return defaultsConfigurationPath;
    }

    public String getStateDatabasePath() {
        return stateDatabasePath;
    }

    public Map<String, String> getJsVariables() {
        return jsVariables;
    }

}
