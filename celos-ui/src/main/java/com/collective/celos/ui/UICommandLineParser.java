package com.collective.celos.ui;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;

public class UICommandLineParser {

    private static final String CLI_CELOS_ADDRESS = "celosAddr";
    private static final String CLI_PORT = "port";

    private static final Logger LOGGER = Logger.getLogger(UICommandLineParser.class);
    
    public UICommandLine parse(final String[] commandLineArguments) throws Exception {

        final CommandLineParser cmdLineGnuParser = new GnuParser();
        final Options gnuOptions = constructOptions();
        CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

        validateHasOption(commandLine, CLI_PORT);
        validateHasOption(commandLine, CLI_CELOS_ADDRESS);

        URL celosAddress = new URL(commandLine.getOptionValue(CLI_CELOS_ADDRESS));
        Integer port = Integer.valueOf(commandLine.getOptionValue(CLI_PORT));

        return new UICommandLine(celosAddress, port);
    }

    private void validateHasOption(CommandLine commandLine, String option) {
        if (!commandLine.hasOption(option)) {
            printHelp(80, 5, 3, true, System.out);
            throw new IllegalArgumentException("Missing --" + option + " argument");
        }
    }

    public Options constructOptions() {
        final Options options = new Options();
        options.addOption(CLI_CELOS_ADDRESS, CLI_CELOS_ADDRESS, true, "Celos Server URL")
               .addOption(CLI_PORT, CLI_PORT, true, "Celos UI server port");
        return options;
    }


    public void printHelp(
            final int printedRowWidth,
            final int spacesBeforeOption,
            final int spacesBeforeOptionDescription,
            final boolean displayUsage,
            final OutputStream out) {
        final String commandLineSyntax = "java -jar <celos-ui>.jar --port <port> --celosAddr <Celos Server URL>";
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
