package com.collective.celos.config;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CliParser {

    public static final String CLI_CELOS_CONF_URI_PARAM = "cd";
    public static final String CLI_PATH_TO_WF_PARAM = "pw";
    public static final String CLI_WF_NAME_PARAM = "wn";
    public static final String CLI_USER_NAME = "u";
    public static final String CLI_MODE = "m";
    public static final String CLI_HDFS_SITE = "h";
    public static final String CLI_CORE_SITE = "c";
    public static final String CLI_TARGET = "t";

    public Config parse(final String[] commandLineArguments) throws ParseException {

        final CommandLineParser cmdLineGnuParser = new GnuParser();
        final Options gnuOptions = constructOptions();
        CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

        Config config = new Config();
        if (commandLine.hasOption(CLI_PATH_TO_WF_PARAM)) {
            config.setPathToWorkflow(commandLine.getOptionValue(CLI_PATH_TO_WF_PARAM));
        }
        if (commandLine.hasOption(CLI_WF_NAME_PARAM)) {
            config.setWorkflowName(commandLine.getOptionValue(CLI_WF_NAME_PARAM));
        }
        if (commandLine.hasOption(CLI_MODE)) {
            Config.Mode mode = Config.Mode.valueOf(commandLine.getOptionValue(CLI_MODE));
            if (mode != null) {
                config.setMode(mode);
            } else {
                System.out.println("Unknown mode: " + commandLine.getOptionValue(CLI_MODE));
                return null;
            }
        }
        if (commandLine.hasOption(CLI_CELOS_CONF_URI_PARAM)) {
            config.setCelosWorkflowsDirUri(commandLine.getOptionValue(CLI_CELOS_CONF_URI_PARAM));
        }
        if (commandLine.hasOption(CLI_USER_NAME)) {
            config.setUserName(commandLine.getOptionValue(CLI_USER_NAME));
        }
        if (commandLine.hasOption(CLI_HDFS_SITE)) {
            config.setPathToHdfsSite(commandLine.getOptionValue(CLI_HDFS_SITE));
        }
        if (commandLine.hasOption(CLI_CORE_SITE)) {
            config.setPathToCoreSite(commandLine.getOptionValue(CLI_CORE_SITE));
        }
        if (commandLine.hasOption(CLI_TARGET)) {
            config.setTargetFile(commandLine.getOptionValue(CLI_TARGET));
        }
        return config;
    }

    public Options constructOptions() {
        final Options options = new Options();
        options.addOption(CLI_CELOS_CONF_URI_PARAM, "celosConfDir", true, "URI of workflow deploy dir on Celos server. Mandatory")
                .addOption(CLI_PATH_TO_WF_PARAM, "pathToWorkflow", true, "Path to a workflow you want to deploy. Mandatory")
                .addOption(CLI_WF_NAME_PARAM, "workflowName", true, "Desired workflow file name. Mandatory")
                .addOption(CLI_USER_NAME, "userName", true, "Username. Defaults to current's user name")
                .addOption(CLI_MODE, "mode", true, "Mode: DEPLOY or UNDEPLOY. Defaults to DEPLOY")
                .addOption(CLI_HDFS_SITE, "hdfsSite", true, "Path to hdfs-site.xml file. Mandatory")
                .addOption(CLI_CORE_SITE, "coreSite", true, "Path to core-site.xml file. Mandatory")
                .addOption(CLI_TARGET, "target", true, "Path to target JSON (alternative to command line)");
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
