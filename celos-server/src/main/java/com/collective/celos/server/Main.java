package com.collective.celos.server;

import java.util.Collections;

public class Main {

    public static void main(String... args) throws Exception {
        ContextParser contextParser = new ContextParser();
        CelosCommandLine commandLine = contextParser.parse(args);
        CelosServer celosServer = new CelosServer();
        celosServer.startServer(commandLine.getPort(),
                Collections.<String, String>emptyMap(),
                commandLine.getWorkflowsDir(),
                commandLine.getDefaultsDir(),
                commandLine.getStateDatabase(),
                commandLine.getUiDir());
    }

}