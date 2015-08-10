package com.collective.celos.server;

import com.collective.celos.Util;

import java.util.Collections;

public class Main {

    public static void main(String... args) throws Exception {
        ServerCommandLineParser serverCommandLineParser = new ServerCommandLineParser();
        ServerCommandLine commandLine = serverCommandLineParser.parse(args);
        CelosServer celosServer = new CelosServer();
        celosServer.startServer(commandLine.getPort(),
                Collections.<String, String>emptyMap(),
                commandLine.getWorkflowsDir(),
                commandLine.getDefaultsDir(),
                commandLine.getStateDatabase(),
                commandLine.getUiDir(),
                commandLine.getVersion());
        Util.setupLogging(commandLine.getLogDir());
    }

}