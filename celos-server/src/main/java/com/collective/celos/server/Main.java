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
import com.google.common.collect.Lists;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Main entry point to the scheduler server.
 */
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
            Timer timer = new Timer(true);
            timer.schedule(createTimerTask(ports), 0, autoSchedule * Constants.SECOND_MS);
        }
    }

    private static TimerTask createTimerTask(final List<Integer> ports) {

        List<CelosClient> clients = ports.stream().map(new Function<Integer, CelosClient>() {
            @Override
            public CelosClient apply(Integer port) {
                return new CelosClient(URI.create("http://localhost:" + port));
            }
        }).collect(Collectors.toList());

        return new TimerTask() {
            @Override
            public void run() {
                try {
                    for (CelosClient celosClient: clients) {
                        celosClient.iterateScheduler();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}