/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

/**
 * Miscellaneous constants.
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
    public static final String SWARM_SIZE = "swarm.size";
    public static final String SWARM_CELOS_NUMBER = "swarm.celos.number";

    public static final Integer SECOND_MS = 1000;
}
