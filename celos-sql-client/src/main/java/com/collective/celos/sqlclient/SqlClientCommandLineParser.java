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
package com.collective.celos.sqlclient;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;

public class SqlClientCommandLineParser {

    private static final String JDBC_URL = "jdbcUrl";
    private static final String JDBC_NAME = "jdbcName";
    private static final String JDBC_PASSWORD = "jdbcPassword";
    private static final String SQL_FILE = "sqlFile";

    public SqlClientCommandLine parse(final String[] commandLineArguments) throws Exception {

        final CommandLineParser cmdLineGnuParser = new GnuParser();
        final Options gnuOptions = constructOptions();
        CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

        String url = getRequiredArgument(commandLine, JDBC_URL);
        String username = getRequiredArgument(commandLine, JDBC_NAME);
        String password = getRequiredArgument(commandLine, JDBC_PASSWORD);
        String sqlFile = getRequiredArgument(commandLine, SQL_FILE);

        return new SqlClientCommandLine(url, username, password, new File(sqlFile));
    }

    private String getRequiredArgument(CommandLine commandLine, String argument) {
        if (!commandLine.hasOption(argument)) {
            printHelp(80, 5, 3, true, System.out);
            throw new IllegalArgumentException("Missing --" + argument + " argument");
        }
        return commandLine.getOptionValue(argument);
    }

    public Options constructOptions() {
        final Options options = new Options();
        options.addOption(JDBC_URL, JDBC_URL, true, "JDBC db url")
                .addOption(JDBC_NAME, JDBC_NAME, true, "JDBC db username")
                .addOption(JDBC_PASSWORD, JDBC_PASSWORD, true, "JDBC db password")
                .addOption(SQL_FILE, SQL_FILE, true, "File with SQL commands to be executed line by line");
        return options;
    }


    public void printHelp(
            final int printedRowWidth,
            final int spacesBeforeOption,
            final int spacesBeforeOptionDescription,
            final boolean displayUsage,
            final OutputStream out) {
        final String commandLineSyntax = "java -jar <celos>.jar";
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
