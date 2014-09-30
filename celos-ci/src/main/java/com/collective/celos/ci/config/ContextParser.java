package com.collective.celos.ci.config;

import com.collective.celos.ci.CelosCi;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.config.deploy.CelosCiTargetParser;
import com.collective.celos.ci.config.testing.TestContext;
import com.collective.celos.ci.deploy.JScpWorker;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

public class ContextParser {

    private final String HDFS_PREFIX_PATTERN = "/user/%s/test/%s/%s";

    public static final String CLI_TARGET = "t";
    public static final String CLI_MODE = "m";
    public static final String CLI_DEPLOY_DIR = "d";
    public static final String CLI_WORKFLOW_NAME = "w";
    public static final String CLI_TEST_CASES_DIR = "tc";

    private CelosCiTargetParser targetParser;
    private String userName;

    public ContextParser(CelosCiTargetParser targetParser, String userName) {
        this.targetParser = targetParser;
        this.userName = userName;
    }

    public CelosCi parse(final String[] commandLineArguments) throws Exception {

        final CommandLineParser cmdLineGnuParser = new GnuParser();
        final Options gnuOptions = constructOptions();
        CommandLine commandLine = cmdLineGnuParser.parse(gnuOptions, commandLineArguments);

        if (!commandLine.hasOption(CLI_TARGET) || !commandLine.hasOption(CLI_DEPLOY_DIR) || !commandLine.hasOption(CLI_WORKFLOW_NAME)) {
            printHelp(80, 5, 3, true, System.out);
            return null;
        }

        CelosCiContext.Mode mode = CelosCiContext.Mode.valueOf(commandLine.getOptionValue(CLI_MODE));
        File deployDir = new File(commandLine.getOptionValue(CLI_DEPLOY_DIR));

        String workflowName = commandLine.getOptionValue(CLI_WORKFLOW_NAME);

        CelosCiTarget target = targetParser.parse(commandLine.getOptionValue(CLI_TARGET));


        if (mode == CelosCiContext.Mode.TEST) {

            String testCasesDir = commandLine.getOptionValue(CLI_TEST_CASES_DIR);

            TestContext testContext = prepareTestContext(userName, workflowName, testCasesDir);
            String substitutedCelosWorkflowDir = testContext.getCelosWorkflowDir().getAbsolutePath();
            CelosCiTarget testTarget = new CelosCiTarget(target.getScpSecuritySettings(), target.getPathToHdfsSite(), target.getPathToCoreSite(), substitutedCelosWorkflowDir, target.getDefaultsFile());

            CelosCiContext ciContext = new CelosCiContext(testTarget, userName, mode, deployDir, workflowName, testContext.getHdfsPrefix());
            return new CelosCi(ciContext, testContext);

        } else {
            CelosCiContext ciContext = new CelosCiContext(target, userName, mode, deployDir, workflowName, "");
            return new CelosCi(ciContext, null);
        }


    }

    public TestContext prepareTestContext(String userName, String workflowName, String testCasesDir) throws Exception {

        Path tempDir = Files.createTempDirectory("celos");
        File celosWorkDir = tempDir.toFile();
        System.out.println("Temp dir for Celos is " + tempDir.toAbsolutePath().toString());

        String hdfsPrefix = String.format(HDFS_PREFIX_PATTERN, userName, workflowName, UUID.randomUUID().toString());
        System.out.println("HDFS prefix is: " + hdfsPrefix);

        TestContext context = new TestContext(celosWorkDir, hdfsPrefix, testCasesDir);

        return context;
    }

    public Options constructOptions() {
        final Options options = new Options();
        options.addOption(CLI_TARGET, "target", true, "Path to target JSON (alternative to command line)")
               .addOption(CLI_MODE, "mode", true, "Mode. Defaults to DEPLOY")
               .addOption(CLI_DEPLOY_DIR, "deployDir", true, "Deploy directory. Path to workflow you want to deploy")
               .addOption(CLI_WORKFLOW_NAME, "workflowName", true, "Workflow JS file name")
               .addOption(CLI_TEST_CASES_DIR, "testDir", true, "Test cases dir");
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
