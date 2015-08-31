package com.collective.celos.server;

import com.collective.celos.CelosClient;
import com.collective.celos.Constants;
import com.collective.celos.Util;

import java.net.URI;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String... args) throws Exception {
        ServerCommandLineParser serverCommandLineParser = new ServerCommandLineParser();
        final ServerCommandLine commandLine = serverCommandLineParser.parse(args);
        CelosServer celosServer = new CelosServer();
        celosServer.startServer(commandLine.getPort(),
                Collections.<String, String>emptyMap(),
                commandLine.getWorkflowsDir(),
                commandLine.getDefaultsDir(),
                commandLine.getStateDatabase());

        if (commandLine.getAutoSchedule() > 0) {
            Timer timer = new Timer(true);
            timer.schedule(createTimerTask(commandLine.getPort()), 0, commandLine.getAutoSchedule());
        }

        Util.setupLogging(commandLine.getLogDir());
    }

    private static TimerTask createTimerTask(final int port) {
        final CelosClient celosClient = new CelosClient(URI.create("http://localhost:" + port));

        return new TimerTask() {
            @Override
            public void run() {
                try {
                    celosClient.iterateScheduler();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}