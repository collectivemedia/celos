package com.collective.celos.server;

import com.collective.celos.Constants;
import com.collective.celos.JettyServer;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.Map;

public class CelosServer {

    private JettyServer server = new JettyServer();

    public int startServer(Map<String, String> jsVariables, File workflowsDir, File defaultsDir, File stateDatabase) throws Exception {
        validateDirExists(workflowsDir);
        validateDirExists(defaultsDir);
        validateDirExists(stateDatabase);

        int port = server.start();

        setupContext(jsVariables, workflowsDir, defaultsDir, stateDatabase);
        return port;
    }

    public void startServer(int port, Map<String, String> jsVariables, File workflowsDir, File defaultsDir, File stateDatabase) throws Exception {

        validateDirExists(workflowsDir);
        validateDirExists(defaultsDir);
        validateDirExists(stateDatabase);

        server.start(port);

        setupContext(jsVariables, workflowsDir, defaultsDir, stateDatabase);
    }

    private void setupContext(Map<String, String> jsVariables, File workflowsDir, File defaultsDir, File stateDatabase) {
        Map<String, Object> attributes = ImmutableMap.<String, Object>of(Constants.ADDITIONAL_JS_VARIABLES, jsVariables);
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