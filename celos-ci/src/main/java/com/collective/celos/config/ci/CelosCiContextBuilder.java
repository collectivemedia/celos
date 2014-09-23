package com.collective.celos.config.ci;

import com.collective.celos.deploy.deployer.JScpWorker;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

public class CelosCiContextBuilder {

    private final String HDFS_PREFIX_PATTERN = "/user/%s/test/%s/%s";

    public static final String CLI_TARGET = "t";
    public static final String CLI_MODE = "m";
    public static final String CLI_DEPLOY_DIR = "d";
    public static final String CLI_WORKFLOW_NAME = "w";

    public CelosCiContext parse(final String[] commandLineArguments) throws Exception {

        final CommandLineParser cmdLineGnuParser = new GnuParser();
        final Options gnuOptions = constructOptions();
        CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

        if (!commandLine.hasOption(CLI_TARGET) || !commandLine.hasOption(CLI_DEPLOY_DIR) || !commandLine.hasOption(CLI_WORKFLOW_NAME)) {
            return null;
        }

        CelosCiContext.Mode mode = CelosCiContext.Mode.valueOf(commandLine.getOptionValue(CLI_MODE));
        File deployDir = new File(commandLine.getOptionValue(CLI_DEPLOY_DIR));

        String userName = System.getProperty("user.name");

        String workflowName = commandLine.getOptionValue(CLI_WORKFLOW_NAME);

        CelosCiTargetParser parcer = new CelosCiTargetParser(userName, JScpWorker.DEFAULT_SECURITY_SETTINGS);
        CelosCiTarget target = parcer.parse(commandLine.getOptionValue(CLI_TARGET));

        String hdfsPrefix;
        if (mode == CelosCiContext.Mode.TEST) {
            hdfsPrefix = String.format(HDFS_PREFIX_PATTERN, userName, workflowName, UUID.randomUUID().toString());
        } else {
            hdfsPrefix = "";
        }

        CelosCiContext context = new CelosCiContext(target, userName, mode, deployDir, workflowName, hdfsPrefix);

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
