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
package com.collective.celos.server;

import com.collective.celos.Constants;
import com.collective.celos.JettyServer;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.Map;

/**
 * HTTP server wrapping a scheduler and providing the HTTP API.
 */
public class CelosServer {

    private JettyServer server = new JettyServer();

    public int startServer(Map<String, String> jsVariables,
                           File workflowsDir, File defaultsDir, File stateDatabase) throws Exception {
        return startServer(jsVariables, workflowsDir, defaultsDir, stateDatabase, 1, 0);
    }

    public void startServer(int port, Map<String, String> jsVariables,
                            File workflowsDir, File defaultsDir, File stateDatabase) throws Exception {
        startServer(port, jsVariables, workflowsDir, defaultsDir, stateDatabase, 1, 0);
    }

    public int startServer(Map<String, String> jsVariables,
                           File workflowsDir, File defaultsDir, File stateDatabase,
                           int swarmSize, int celosNumber) throws Exception {

        validateDirExists(workflowsDir);
        validateDirExists(defaultsDir);
        validateDirExists(stateDatabase);

        int port = server.start();

        setupContext(jsVariables, workflowsDir, defaultsDir, stateDatabase, swarmSize, celosNumber);
        return port;
    }

    public void startServer(int port, Map<String, String> jsVariables,
                            File workflowsDir, File defaultsDir, File stateDatabase,
                            int swarmSize, int celosNumber) throws Exception {

        validateDirExists(workflowsDir);
        validateDirExists(defaultsDir);
        validateDirExists(stateDatabase);

        server.start(port);

        setupContext(jsVariables, workflowsDir, defaultsDir, stateDatabase, swarmSize, celosNumber);
    }

    private void setupContext(Map<String, String> jsVariables, File workflowsDir, File defaultsDir, File stateDatabase, int swarmSize, int celosNumber) {
        Map<String, Object> attributes = ImmutableMap.of(Constants.ADDITIONAL_JS_VARIABLES, jsVariables,
                Constants.SWARM_SIZE, swarmSize,
                Constants.SWARM_CELOS_NUMBER, celosNumber);

        Map<String, String> initParams = ImmutableMap.of(Constants.WORKFLOW_CONFIGURATION_PATH_ATTR, workflowsDir.getAbsolutePath(),
                Constants.DEFAULTS_CONFIGURATION_PATH_ATTR, defaultsDir.getAbsolutePath(),
                Constants.STATE_DATABASE_PATH_ATTR, stateDatabase.getAbsolutePath());

        server.setupContext(attributes, initParams);
    }

    private void validateDirExists(File dir) {
        if (dir == null || !dir.isDirectory() || !dir.exists()) {
            throw new IllegalStateException("Cannot start server: " + dir + " doesnt exist");
        }
    }

    public void stopServer() throws Exception {
        server.stop();
    }

}