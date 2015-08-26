package com.collective.celos;

/**
 * Created by akonopko on 24.07.15.
 */
public class Constants {
    
    public static final String DEFAULT_WORKFLOWS_DIR = "/etc/celos/workflows";
    public static final String DEFAULT_DEFAULTS_DIR = "/etc/celos/defaults";
    public static final String DEFAULT_DB_DIR = "/var/lib/celos/db";
    public static final String DEFAULT_LOG_DIR = "/var/log/celos";
    public static final String DEFAULT_HDFS_ROOT = "/user/celos/app";

    public static final String WORKFLOW_CONFIGURATION_PATH_ATTR = "workflow.configuration.path";
    public static final String DEFAULTS_CONFIGURATION_PATH_ATTR = "defaults.configuration.path";
    public static final String STATE_DATABASE_PATH_ATTR = "state.database.path";
    public static final String ADDITIONAL_JS_VARIABLES = "additional.js.variables";
    public static final int MINUTE_MS = 60000;

}
