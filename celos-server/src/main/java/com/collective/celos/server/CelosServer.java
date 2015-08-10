package com.collective.celos.server;

import com.collective.celos.Constants;
import com.collective.celos.JettyServer;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.util.Map;

public class CelosServer {

    private JettyServer server = new JettyServer();

    public void startServer(int port, Map<String, String> jsVariables, File workflowsDir,
                            File defaultsDir, File stateDatabase, File uiDir, String version) throws Exception {

        validateDirExists(workflowsDir);
        validateDirExists(defaultsDir);
        validateDirExists(stateDatabase);
        validateDirExists(uiDir);

        server.start(port);
        Map<String, Object> attributes = ImmutableMap.<String, Object>of(Constants.ADDITIONAL_JS_VARIABLES, jsVariables);

        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put(Constants.WORKFLOW_CONFIGURATION_PATH_ATTR, workflowsDir.getAbsolutePath());
        builder.put(Constants.DEFAULTS_CONFIGURATION_PATH_ATTR, defaultsDir.getAbsolutePath());
        builder.put(Constants.STATE_DATABASE_PATH_ATTR, stateDatabase.getAbsolutePath());
        builder.put(Constants.UI_PATH_ATTR, uiDir.getAbsolutePath());
        builder.put(Constants.VERSION_ATTR, version);
        Map<String, String> initParams = builder.build();

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
