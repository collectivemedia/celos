package com.collective.celos.ci.config;

import org.apache.commons.cli.*;

import java.io.OutputStream;
import java.io.PrintWriter;

public class ContextParser {

    public static final String CLI_TARGET = "target";
    public static final String CLI_MODE = "mode";
    public static final String CLI_DEPLOY_DIR = "deployDir";
    public static final String CLI_WORKFLOW_NAME = "workflowName";
    public static final String CLI_TEST_CASES_DIR = "testDir";

    public CelosCiCommandLine parse(final String[] commandLineArguments) throws Exception {

        final CommandLineParser cmdLineGnuParser = new GnuParser();
        final Options gnuOptions = constructOptions();
        CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

        if (!commandLine.hasOption(CLI_TARGET) || !commandLine.hasOption(CLI_MODE) || !commandLine.hasOption(CLI_WORKFLOW_NAME)) {
            printHelp(80, 5, 3, true, System.out);
            throw new RuntimeException("Wrong CelosCi configuration provided");
        }

        String deployDir = commandLine.getOptionValue(CLI_DEPLOY_DIR);
        String mode = commandLine.getOptionValue(CLI_MODE);
        String workflowName = commandLine.getOptionValue(CLI_WORKFLOW_NAME);
        String targetUri = commandLine.getOptionValue(CLI_TARGET);
        String testCasesDir = commandLine.getOptionValue(CLI_TEST_CASES_DIR);
        String userName = System.getenv("username") == null ? System.getProperty("user.name") : System.getenv("username");

        return new CelosCiCommandLine(targetUri, mode, deployDir, workflowName, testCasesDir, userName);
    }

    public Options constructOptions() {
        final Options options = new Options();
        options.addOption(CLI_TARGET, CLI_TARGET, true, "Path to target JSON (alternative to command line)")
                .addOption(CLI_MODE, CLI_MODE, true, "Mode. Defaults to DEPLOY")
                .addOption(CLI_DEPLOY_DIR, CLI_DEPLOY_DIR, true, "Deploy directory. Path to workflow you want to deploy")
                .addOption(CLI_WORKFLOW_NAME, CLI_WORKFLOW_NAME, true, "Workflow JS file name")
                .addOption(CLI_TEST_CASES_DIR, CLI_TEST_CASES_DIR, true, "Test cases dir");
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
                constructOptions(),
                spacesBeforeOption,
                spacesBeforeOptionDescription,
                null,
                displayUsage);
        writer.flush();
    }

}
