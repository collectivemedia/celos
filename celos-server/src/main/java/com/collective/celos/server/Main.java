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

import com.collective.celos.Util;

import java.util.Collections;

/**
 * Main entry point to the scheduler server.
 */
public class Main {

    public static void main(String... args) throws Exception {

        ServerCommandLineParser serverCommandLineParser = new ServerCommandLineParser();
        final ServerCommandLine commandLine = serverCommandLineParser.parse(args);
        Util.setupLogging(commandLine.getLogDir());

        CelosServer celosServer = new CelosServer();
        celosServer.startServer(commandLine.getPort(),
                Collections.emptyMap(),
                commandLine.getWorkflowsDir(),
                commandLine.getDefaultsDir(),
                commandLine.getDatabase());

        if (commandLine.getAutoSchedule() > 0) {
            CelosIterator celosIterator = new CelosIterator(commandLine.getPort(), commandLine.getAutoSchedule());
            if (commandLine.getZookeeperConnection() != null) {
                ZookeeperSupport zookeeperSupport = new ZookeeperSupport(commandLine.getZookeeperConnection());
                celosIterator.start(zookeeperSupport);
            } else {
                celosIterator.start();
            }
        }

    }

}