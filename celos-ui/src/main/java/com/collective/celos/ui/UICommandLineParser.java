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
package com.collective.celos.ui;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;

/**
 * Parses for UI command-line options.
 */
public class UICommandLineParser {

    private static final String CLI_CELOS_ADDRESS = "celos";
    private static final String CLI_HUE_ADDRESS = "hue";
    private static final String CLI_PORT = "port";
    private static final String CLI_CONFIG = "config";

    private static final Logger LOGGER = Logger.getLogger(UICommandLineParser.class);
    
    public UICommandLine parse(final String[] commandLineArguments) throws Exception {

        final org.apache.commons.cli.CommandLineParser cmdLineGnuParser = new GnuParser();
        final Options gnuOptions = constructOptions();
        org.apache.commons.cli.CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

        validateHasOption(commandLine, CLI_PORT);
        validateHasOption(commandLine, CLI_CELOS_ADDRESS);

        URL celosAddress = new URL(commandLine.getOptionValue(CLI_CELOS_ADDRESS));
        Integer port = Integer.valueOf(commandLine.getOptionValue(CLI_PORT));
        URL hueAddress = commandLine.hasOption(CLI_HUE_ADDRESS) ? new URL(commandLine.getOptionValue(CLI_HUE_ADDRESS)) : null;

        File configFile = null;
        if (commandLine.hasOption(CLI_CONFIG)) {
            configFile = new File(commandLine.getOptionValue(CLI_CONFIG));
            if (!configFile.exists()) {
                throw new IllegalArgumentException("Provided UI config file " + configFile.getPath() + " does not exist");
            }
        }

        return new UICommandLine(celosAddress, hueAddress, port, configFile);
    }

    private void validateHasOption(org.apache.commons.cli.CommandLine commandLine, String option) {
        if (!commandLine.hasOption(option)) {
            printHelp(80, 5, 3, true, System.out);
            throw new IllegalArgumentException("Missing --" + option + " argument");
        }
    }

    public Options constructOptions() {
        final Options options = new Options();
        options.addOption(CLI_CELOS_ADDRESS, CLI_CELOS_ADDRESS, true, "Celos Server URL")
               .addOption(CLI_HUE_ADDRESS, CLI_HUE_ADDRESS, true, "Hue (Oozie UI) URL")
               .addOption(CLI_PORT, CLI_PORT, true, "Celos UI server port")
               .addOption(CLI_CONFIG, CLI_CONFIG, true, "Celos UI config");
        return options;
    }


    public void printHelp(
            final int printedRowWidth,
            final int spacesBeforeOption,
            final int spacesBeforeOptionDescription,
            final boolean displayUsage,
            final OutputStream out) {
        final String commandLineSyntax = "java -jar <celos-ui>.jar --port <port> --celos <Celos Server URL> [--config <config.json>] [--hue <Hue URL>]";
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(
                writer,
                printedRowWidth,
                commandLineSyntax,
                null,
                constructOptions(),
                spacesBeforeOption,
                spacesBeforeOptionDescription,
                null,
                displayUsage);
        writer.flush();
    }

}
