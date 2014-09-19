package com.collective.celos.ci.config;

import com.collective.celos.cd.config.CelosCdTarget;
import com.collective.celos.cd.config.TargetParser;
import com.collective.celos.cd.deployer.JScpWorker;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;

public class TestContextBuilder {

    public static final String CLI_TARGET = "t";
    public static final String CLI_DEPLOY_DIR = "d";
    public static final String CLI_WORKFLOW_NAME = "w";

    public TestContext parse(final String[] commandLineArguments) throws Exception {

        final CommandLineParser cmdLineGnuParser = new GnuParser();
        final Options gnuOptions = constructOptions();
        CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

        if (!commandLine.hasOption(CLI_DEPLOY_DIR) || !commandLine.hasOption(CLI_TARGET) || !commandLine.hasOption(CLI_WORKFLOW_NAME)) {
            return null;
        }

        String deployDir = commandLine.getOptionValue(CLI_DEPLOY_DIR);
        String userName = System.getProperty("user.name");

        TestConfigBuilder testConfigParcer = new TestConfigBuilder(userName, JScpWorker.DEFAULT_SECURITY_SETTINGS);
        TestConfig testConfig = testConfigParcer.build(new File(deployDir, "test-config.json").toString());

        TargetParser targetParcer = new TargetParser(userName, JScpWorker.DEFAULT_SECURITY_SETTINGS);
        CelosCdTarget target = targetParcer.parse(commandLine.getOptionValue(CLI_TARGET));

        String workflowName = commandLine.getOptionValue(CLI_WORKFLOW_NAME);

        TestContext context = new TestContext(testConfig, target, workflowName, deployDir, System.getProperty("user.name"));
        return context;
    }

    public Options constructOptions() {
        final Options options = new Options();
        options.addOption(CLI_TARGET, "target", true, "Path to target JSON (alternative to command line)")
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
