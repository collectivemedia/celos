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
import com.collective.celos.Scheduler;
import com.collective.celos.Util;
import com.collective.celos.database.StateDatabase;
import com.collective.celos.servlet.AbstractServlet;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.Map;

/**
 * HTTP server wrapping a scheduler and providing the HTTP API.
 */
public class CelosServer {

    private JettyServer server = new JettyServer();

    public int startServer(Map<String, String> jsVariables, File workflowsDir, File defaultsDir, StateDatabase.Config config) throws Exception {
        Util.validateDirExists(workflowsDir);
        Util.validateDirExists(defaultsDir);
        config.validate();

        int port = server.start();

        setupContext(jsVariables, workflowsDir, defaultsDir, config);
        return port;
    }

    public void startServer(int port, Map<String, String> jsVariables, File workflowsDir, File defaultsDir, StateDatabase.Config config) throws Exception {

        Util.validateDirExists(workflowsDir);
        Util.validateDirExists(defaultsDir);
        config.validate();

        server.start(port);

        setupContext(jsVariables, workflowsDir, defaultsDir, config);
    }

    private void setupContext(Map<String, String> jsVariables, File workflowsDir, File defaultsDir, StateDatabase.Config config) {
        Map<String, Object> attributes = ImmutableMap.of(
                Constants.ADDITIONAL_JS_VARIABLES, jsVariables,
                Constants.DATABASE_CONFIG, config
        );
        Map<String, String> initParams = ImmutableMap.of(
                Constants.WORKFLOW_CONFIGURATION_PATH_ATTR, workflowsDir.getAbsolutePath(),
                Constants.DEFAULTS_CONFIGURATION_PATH_ATTR, defaultsDir.getAbsolutePath()
        );

        server.setupContext(attributes, initParams);
    }

    public void stopServer() throws Exception {
        server.stop();
    }

    public Scheduler getScheduler() {
        return (Scheduler) server.getContext().getAttribute(AbstractServlet.SCHEDULER_ATTR);
    }

}