package com.collective.celos.server;

import com.collective.celos.CelosClient;
import com.collective.celos.Constants;
import com.collective.celos.Util;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String... args) throws Exception {
        ServerCommandLineParser serverCommandLineParser = new ServerCommandLineParser();
        final ServerCommandLine commandLine = serverCommandLineParser.parse(args);

        List<Integer> ports = Lists.newArrayList();

        CelosServer mainCelosServer = new CelosServer();
        mainCelosServer.startServer(commandLine.getPort(),
                Collections.<String, String>emptyMap(),
                commandLine.getWorkflowsDir(),
                commandLine.getDefaultsDir(),
                commandLine.getStateDatabase(),
                commandLine.getParallelismLevel(), 0);

        ports.add(commandLine.getPort());

        for (int i = 1; i < commandLine.getParallelismLevel(); i++) {
            CelosServer celosServer = new CelosServer();
            int port = celosServer.startServer(Collections.<String, String>emptyMap(),
                    commandLine.getWorkflowsDir(),
                    commandLine.getDefaultsDir(),
                    commandLine.getStateDatabase(),
                    commandLine.getParallelismLevel(), i);
            ports.add(port);
        }

        setupAutoschedule(ports, commandLine.getAutoSchedule());

        Util.setupLogging(commandLine.getLogDir());
    }

    static void setupAutoschedule(List<Integer> ports, int autoSchedule) {
        if (autoSchedule > 0) {

            for (Integer port : ports) {
                new Thread(new Runnable() {
                    CelosClient celosClient = new CelosClient(URI.create("http://localhost:" + port));

                    @Override
                    public void run() {
                        while (true) {
                            try {
                                long timeStart = System.currentTimeMillis();
                                celosClient.iterateScheduler();
                                long duration = System.currentTimeMillis() - timeStart;
                                if (duration < autoSchedule * Constants.SECOND_MS) {
                                    Thread.sleep(autoSchedule * Constants.SECOND_MS - duration);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }).start();
            }
        }
    }

}