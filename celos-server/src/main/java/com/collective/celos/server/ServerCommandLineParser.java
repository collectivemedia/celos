package com.collective.celos.server;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import com.collective.celos.Constants;

/**
 * Parses the server command-line options.
 */
public class ServerCommandLineParser {

    private static final String CLI_WF_DIR = "workflows";
    private static final String CLI_DEFAULTS_DIR = "defaults";
    private static final String CLI_STATE_DB_DIR = "db";
    private static final String CLI_LOG_DIR = "logs";
    private static final String CLI_AUTOSCHEDULE = "autoSchedule";
    private static final String CLI_PORT = "port";

    private static final Logger LOGGER = Logger.getLogger(ServerCommandLineParser.class);
    
    public ServerCommandLine parse(final String[] commandLineArguments) throws Exception {

        final CommandLineParser cmdLineGnuParser = new GnuParser();
        final Options gnuOptions = constructOptions();
        CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

        if (!commandLine.hasOption(CLI_PORT)) {
            printHelp(80, 5, 3, true, System.out);
            throw new IllegalArgumentException("Missing --" + CLI_PORT + " argument");
        }

        String stateDbDir = getDefault(commandLine, CLI_STATE_DB_DIR, Constants.DEFAULT_DB_DIR);
        String defaultsDir = getDefault(commandLine, CLI_DEFAULTS_DIR, Constants.DEFAULT_DEFAULTS_DIR);
        String workflowsDir = getDefault(commandLine, CLI_WF_DIR, Constants.DEFAULT_WORKFLOWS_DIR);
        String logDir = getDefault(commandLine, CLI_LOG_DIR, Constants.DEFAULT_LOG_DIR);
        Integer autoSchedule = Integer.valueOf(getDefault(commandLine, CLI_AUTOSCHEDULE, "-1"));
        Integer port = Integer.valueOf(commandLine.getOptionValue(CLI_PORT));

        return new ServerCommandLine(workflowsDir, defaultsDir, stateDbDir, logDir, port, autoSchedule);
    }

    private String getDefault(CommandLine commandLine, String optionName, String defaultValue) {
        String value = commandLine.getOptionValue(optionName);
        if (value == null) {
            LOGGER.info("--" + optionName + " not specified, using default value: " + defaultValue);
            return defaultValue;
        } else {
            return value;
        }
    }

    public Options constructOptions() {
        final Options options = new Options();
        options.addOption(CLI_WF_DIR, CLI_WF_DIR, true, "Path to WORKFLOWS dir")
                .addOption(CLI_DEFAULTS_DIR, CLI_DEFAULTS_DIR, true, "Path to DEFAULTS dir")
                .addOption(CLI_STATE_DB_DIR, CLI_STATE_DB_DIR, true, "Path to STATE DATABASE dir")
                .addOption(CLI_PORT, CLI_PORT, true, "Celos Server port")
                .addOption(CLI_LOG_DIR, CLI_LOG_DIR, true, "Celos logs dir")
                .addOption(CLI_AUTOSCHEDULE, CLI_AUTOSCHEDULE, true, "Time period in seconds to automatically run Scheduler. If not specified, Scheduler will not be automatically run");
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
