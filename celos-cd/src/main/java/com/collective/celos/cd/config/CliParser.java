package com.collective.celos.cd.config;

import org.apache.commons.cli.*;

import java.io.OutputStream;
import java.io.PrintWriter;

public class CliParser {

    public static final String CLI_TARGET = "t";
    public static final String CLI_MODE = "m";

    public Config parse(final String[] commandLineArguments) throws Exception {

        final CommandLineParser cmdLineGnuParser = new GnuParser();
        final Options gnuOptions = constructOptions();
        CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

        if (!commandLine.hasOption(CLI_TARGET)) {
            throw new RuntimeException("Please, specify target config json");
        }
        Config.Mode mode = null;
        if (!commandLine.hasOption(CLI_MODE)) {
            mode = Config.Mode.DEPLOY;
        } else {
            Config.Mode.valueOf(commandLine.getOptionValue(CLI_MODE));
        }

        ConfigBuilder parcer = new ConfigBuilder(System.getProperty("user.name"), Config.DEFAULT_SECURITY_SETTINGS, commandLine.getOptionValue(CLI_TARGET), mode);
        return parcer.build();

    }

    public Options constructOptions() {
        final Options options = new Options();
        options.addOption(CLI_TARGET, "target", true, "Path to target JSON (alternative to command line)")
               .addOption(CLI_MODE, "mode", true, "Mode. Defaults to DEPLOY");
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
