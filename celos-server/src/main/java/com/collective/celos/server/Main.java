package com.collective.celos.server;

import com.collective.celos.CelosClient;
import com.collective.celos.Constants;
import com.collective.celos.Util;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.*;

public class Main {

    public static void main(String... args) throws Exception {

        args = "-parallel 5 -autoSchedule 60 -db /home/akonopko/celos_wrk/db -defaults /home/akonopko/celos_wrk/defaults -logs /home/akonopko/celos_wrk/logs -workflows /home/akonopko/celos_wrk/workflows -port 9123".split(" ");

        ServerCommandLineParser serverCommandLineParser = new ServerCommandLineParser();
        final ServerCommandLine commandLine = serverCommandLineParser.parse(args);

        Util.setupLogging(commandLine.getLogDir());

        List<Integer> ports = Lists.newArrayList();
        ports.add(commandLine.getPort());

        for (int i = 1; i < commandLine.getParallelismLevel(); i++) {
            CelosServer celosServer = new CelosServer();
            int port = celosServer.startServer(Collections.<String, String>emptyMap(),
                    commandLine.getWorkflowsDir(),
                    commandLine.getDefaultsDir(),
                    commandLine.getStateDatabase(),
                    commandLine.getParallelismLevel(), i);
            ports.add(port);
            System.out.println("start...");
        }

        CelosServer mainCelosServer = new CelosServer();
        mainCelosServer.startServer(commandLine.getPort(),
                Collections.<String, String>emptyMap(),
                commandLine.getWorkflowsDir(),
                commandLine.getDefaultsDir(),
                commandLine.getStateDatabase(),
                commandLine.getParallelismLevel(), 0, ports);

        setupAutoSchedule(commandLine, ports);
        System.out.println("Server is started");
    }

    private static void setupAutoSchedule(ServerCommandLine commandLine, List<Integer> slavePorts) throws InterruptedException {
        int autoSchedule = commandLine.getAutoSchedule();
        if (autoSchedule > 0) {

            List<Integer> autoSchedulePorts = new ArrayList<>(slavePorts);
            autoSchedulePorts.add(commandLine.getPort());

            int delay = autoSchedule * Constants.SECOND_MS / autoSchedulePorts.size();
            int j = 0;
            for (Integer port : autoSchedulePorts) {
                createAutoScheduleThread(autoSchedule, port, delay * (j++)).start();
            }
        }
    }

    private static Thread createAutoScheduleThread(final int autoSchedule, final Integer port, int delay) {
        return new Thread(new Runnable() {
            CelosClient celosClient = new CelosClient(URI.create("http://localhost:" + port));

            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true) {
                    try {
                        long timeStart = System.currentTimeMillis();
                        celosClient.iterateScheduler();
                        long duration = System.currentTimeMillis() - timeStart;
                        LOGGER.info("Step time on " + port + " is " + duration);

                        if (duration < autoSchedule * Constants.SECOND_MS) {
                            Thread.sleep(autoSchedule * Constants.SECOND_MS - duration);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private static Logger LOGGER = Logger.getLogger(Main.class);

}