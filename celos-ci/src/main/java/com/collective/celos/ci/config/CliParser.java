package com.collective.celos.ci.config;

import java.io.OutputStream;
import java.io.PrintWriter;

import com.collective.celos.cd.config.Config;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CliParser {

    public static void main(String... arg) {

    }

//    public static final String CLI_TEST_CONFIG = "c";
//    public static final String CLI_TARGET = "t";
//
//    public Config parse(final String[] commandLineArguments) throws ParseException {
//
//        final CommandLineParser cmdLineGnuParser = new GnuParser();
//        final Options gnuOptions = constructOptions();
//        CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);
//
//        Config config = new Config();
//        if (commandLine.hasOption(CLI_TARGET)) {
//
//        }
//        if (commandLine.hasOption(CLI_TEST_CONFIG)) {
//
//        }
//
//        return config;
//    }
//
//    public Options constructOptions() {
//        final Options options = new Options();
//        options.addOption(CLI_TARGET, "target", true, "Path to target JSON");
//        options.addOption(CLI_TEST_CONFIG, "testTarget", true, "Path to test config JSON");
//        return options;
//    }
//
//
//    public void printHelp(
//            final int printedRowWidth,
//            final int spacesBeforeOption,
//            final int spacesBeforeOptionDescription,
//            final boolean displayUsage,
//            final OutputStream out) {
//        final String commandLineSyntax = "hadoop jar <celos cd jarname>.jar";
//        final PrintWriter writer = new PrintWriter(out);
//        final HelpFormatter helpFormatter = new HelpFormatter();
//        helpFormatter.printHelp(
//                writer,
//                printedRowWidth,
//                commandLineSyntax,
//                null,
//                constructOptions(),
//                spacesBeforeOption,
//                spacesBeforeOptionDescription,
//                null,
//                displayUsage);
//        writer.flush();
//    }

}
