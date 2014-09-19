package com.collective.celos.cd.config;

import com.collective.celos.cd.deployer.JScpWorker;
import org.apache.commons.cli.*;

import java.io.OutputStream;
import java.io.PrintWriter;

public class CelosCdContextBuilder {

    public static final String CLI_TARGET = "t";
    public static final String CLI_MODE = "m";
    public static final String CLI_DEPLOY_DIR = "d";
    public static final String CLI_WORKFLOW_NAME = "w";

    public CelosCdContext parse(final String[] commandLineArguments) throws Exception {

        final CommandLineParser cmdLineGnuParser = new GnuParser();
        final Options gnuOptions = constructOptions();
        CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

        if (!commandLine.hasOption(CLI_TARGET) || !commandLine.hasOption(CLI_DEPLOY_DIR) || !commandLine.hasOption(CLI_WORKFLOW_NAME)) {
            return null;
        }

        CelosCdContext.Mode mode = null;
        if (!commandLine.hasOption(CLI_MODE)) {
            mode = CelosCdContext.Mode.DEPLOY;
        } else {
            CelosCdContext.Mode.valueOf(commandLine.getOptionValue(CLI_MODE));
        }
        String userName = System.getProperty("user.name");
        TargetParser parcer = new TargetParser(userName, JScpWorker.DEFAULT_SECURITY_SETTINGS);
        CelosCdTarget target = parcer.parse(commandLine.getOptionValue(CLI_TARGET));

        CelosCdContext context = new CelosCdContext(target,
                userName, mode, commandLine.getOptionValue(CLI_DEPLOY_DIR),
                commandLine.getOptionValue(CLI_WORKFLOW_NAME), null, null);

        return context;
    }

    public Options constructOptions() {
        final Options options = new Options();
        options.addOption(CLI_TARGET, "target", true, "Path to target JSON (alternative to command line)")
               .addOption(CLI_MODE, "mode", true, "Mode. Defaults to DEPLOY")
               .addOption(CLI_DEPLOY_DIR, "deployDir", true, "Deploy directory. Path to workflow you want to deploy")
               .addOption(CLI_WORKFLOW_NAME, "workflowName", true, "Workflow JS file name");
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
