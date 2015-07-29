package com.collective.celos.server;

import org.apache.commons.cli.*;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.SecureRandom;

public class ContextParser {

    private static final String CLI_WF_DIR = "workflowsDir";
    private static final String CLI_DEFAULTS_DIR = "defaultsDir";
    private static final String CLI_STATE_DB_DIR = "stateDir";
    private static final String CLI_UI_DIR = "uiDir";
    private static final String CLI_PORT = "port";

    public CelosCommandLine parse(final String[] commandLineArguments) throws Exception {

        final CommandLineParser cmdLineGnuParser = new GnuParser();
        final Options gnuOptions = constructOptions();
        CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

        if (!commandLine.hasOption(CLI_PORT)) {
            printHelp(80, 5, 3, true, System.out);
            throw new RuntimeException("Missing --" + CLI_PORT + " argument");
        }

        /** TODO: uncomment this when Celos Server should be configured from CL
        String stateDbDir = commandLine.getOptionValue(CLI_STATE_DB_DIR);
        String defaultsDir = commandLine.getOptionValue(CLI_DEFAULTS_DIR);
        String uiDir = commandLine.getOptionValue(CLI_UI_DIR);
        String workflowsDir = commandLine.getOptionValue(CLI_WF_DIR);
         */

        String stateDbDir = "/var/lib/celos/db";
        String defaultsDir = "/etc/celos/defaults";
        String uiDir = "/etc/celos/ui";
        String workflowsDir = "/etc/celos/workflows";

        Integer port = Integer.valueOf(commandLine.getOptionValue(CLI_PORT));

        return new CelosCommandLine(workflowsDir, defaultsDir, stateDbDir, uiDir, port);
    }

    public Options constructOptions() {
        final Options options = new Options();
        options.addOption(CLI_WF_DIR, CLI_WF_DIR, true, "Path to WORKFLOWS dir")
                .addOption(CLI_DEFAULTS_DIR, CLI_DEFAULTS_DIR, true, "Path to DEFAULTS dir")
                .addOption(CLI_STATE_DB_DIR, CLI_STATE_DB_DIR, true, "Path to STATE DATABASE dir")
                .addOption(CLI_UI_DIR, CLI_UI_DIR, true, "Path to UI dir")
                .addOption(CLI_PORT, CLI_PORT, true, "Celos Server port");
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
