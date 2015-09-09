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
package com.collective.celos.ci.config;

import com.collective.celos.*;
import org.apache.commons.cli.*;
import org.apache.hadoop.fs.Path;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CiCommandLineParser {

    private static final String CLI_TARGET = "target";
    private static final String CLI_MODE = "mode";
    private static final String CLI_DEPLOY_DIR = "deployDir";
    private static final String CLI_WORKFLOW_NAME = "workflowName";
    private static final String CLI_TEST_CASES_DIR = "testDir";
    private static final String CLI_CELOS_SERVER = "celos";
    private static final String CLI_HDFS_ROOT = "hdfsRoot";
    private static final String DEFAULT_TEST_CASES_DIR = "src/test/celos-ci";
    protected static final String USERNAME_ENV_VAR = "CELOS_CI_USERNAME";

    private static final String KEEP_TEMP_DATA = "KEEP_TEMP_DATA";

    public CiCommandLine parse(final String[] commandLineArguments) throws Exception {

        final CommandLineParser cmdLineGnuParser = new GnuParser();
        final Options gnuOptions = constructOptionsForParsing();
        CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

        if (!commandLine.hasOption(CLI_TARGET) || !commandLine.hasOption(CLI_MODE) || !commandLine.hasOption(CLI_WORKFLOW_NAME)) {
            printHelp(80, 5, 3, true, System.out);
            throw new RuntimeException("Wrong CelosCi configuration provided");
        }

        String deployDir = commandLine.getOptionValue(CLI_DEPLOY_DIR);
        String mode = commandLine.getOptionValue(CLI_MODE);
        String workflowName = commandLine.getOptionValue(CLI_WORKFLOW_NAME);
        String targetUri = commandLine.getOptionValue(CLI_TARGET);
        String testCasesDir = commandLine.getOptionValue(CLI_TEST_CASES_DIR, DEFAULT_TEST_CASES_DIR);
        String hdfsRoot = commandLine.getOptionValue(CLI_HDFS_ROOT, Constants.DEFAULT_HDFS_ROOT);
        String celosServerUri = commandLine.getOptionValue(CLI_CELOS_SERVER);

        boolean keepTempData = Boolean.parseBoolean(System.getenv(KEEP_TEMP_DATA));
        String userName = System.getenv(USERNAME_ENV_VAR);
        if (userName == null) {
            userName = System.getProperty("user.name");
        }
        return new CiCommandLine(targetUri, mode, deployDir, workflowName, testCasesDir, userName, keepTempData, celosServerUri, hdfsRoot);
    }

    public Options constructOptionsForHelp() {
        final Options options = new Options();
        options.addOption(CLI_TARGET, CLI_TARGET, true, "Path to target JSON")
                .addOption(CLI_MODE, CLI_MODE, true, "Mode. Defaults to DEPLOY")
                .addOption(CLI_DEPLOY_DIR, CLI_DEPLOY_DIR, true, "Deploy directory. Path to workflow you want to deploy")
                .addOption(CLI_WORKFLOW_NAME, CLI_WORKFLOW_NAME, true, "Workflow JS file name")
                .addOption(CLI_HDFS_ROOT, CLI_HDFS_ROOT, true, "HDFS root. Defaults to " + Constants.DEFAULT_HDFS_ROOT);
        return options;
    }

    public Options constructOptionsForParsing() {
        Options options = constructOptionsForHelp();
        options.addOption(CLI_CELOS_SERVER, CLI_CELOS_SERVER, true, "Celos Server (for TEST mode. Runs against remote Celos server)")
                .addOption(CLI_TEST_CASES_DIR, CLI_TEST_CASES_DIR, true, "Test cases dir (for TEST mode)");
        return options;
    }


    public void printHelp(
            final int printedRowWidth,
            final int spacesBeforeOption,
            final int spacesBeforeOptionDescription,
            final boolean displayUsage,
            final OutputStream out) {
        final String commandLineSyntax = "hadoop jar <celos cd jarname>.jar";
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(
                writer,
                printedRowWidth,
                commandLineSyntax,
                null,
                constructOptionsForHelp(),
                spacesBeforeOption,
                spacesBeforeOptionDescription,
                null,
                displayUsage);
        writer.flush();
    }

}
