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

import com.collective.celos.CelosClient;
import com.collective.celos.Constants;
import com.collective.celos.Util;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main entry point to the scheduler server.
 */
public class Main {

    private static Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String... args) throws Exception {

        ServerCommandLineParser serverCommandLineParser = new ServerCommandLineParser();
        final ServerCommandLine commandLine = serverCommandLineParser.parse(args);
        if(commandLine.getLogStdout()) {
            System.out.println("Logging all messages to stdout");
            Util.setupLoggingToStdout();
        } else if(System.getProperty("log4j.configuration") == null) {
            Util.setupLogging(commandLine.getLogDir());
        }

        CelosServer celosServer = new CelosServer();
        celosServer.startServer(commandLine.getPort(),
                Collections.<String, String>emptyMap(),
                commandLine.getWorkflowsDir(),
                commandLine.getDefaultsDir(),
                commandLine.getDatabase());

        setupAutoschedule(commandLine.getPort(), commandLine.getAutoSchedule());

    }

    static void setupAutoschedule(int port, int autoSchedule) {
        if (autoSchedule > 0) {
            Timer timer = new Timer(true);
            timer.schedule(createTimerTask(port), 0, autoSchedule * Constants.SECOND_MS);
        }
    }

    private static TimerTask createTimerTask(final int port) {
        final CelosClient celosClient = new CelosClient(URI.create("http://localhost:" + port));

        return new TimerTask() {
            @Override
            public void run() {
                try {
                    celosClient.iterateScheduler();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        };
    }
}