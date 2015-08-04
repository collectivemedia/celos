package com.collective.celos.ci.config;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import junit.framework.Assert;
import org.apache.commons.cli.CommandLine;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 10/1/14.
 */
public class CiCommandLineParserTest {

    @Rule
    public TemporaryFolder tempDirRule = new TemporaryFolder();
    private File tmpDir;

    @Before
    public void setUp() throws IOException {
        tmpDir = tempDirRule.newFolder();
        tmpDir.mkdir();
    }

    @Test
    public void testContextParserTest() throws Exception {
        CiCommandLineParser contextParser = new CiCommandLineParser();

        String[] clParams = ("--testDir " + tmpDir.getAbsolutePath() + " --target target --mode TEST --deployDir deploydir --workflowName workflow").split(" ");

        CiCommandLine commandLine = contextParser.parse(clParams);

        Assert.assertEquals(commandLine.getDeployDir(), new File("deploydir"));
        Assert.assertEquals(commandLine.getMode(), CelosCiContext.Mode.TEST);
        Assert.assertEquals(commandLine.getTargetUri(), URI.create("target"));
        Assert.assertEquals(commandLine.getUserName(), System.getProperty("user.name"));
        Assert.assertEquals(commandLine.getWorkflowName(), "workflow");
    }

    @Test
    public void testContextParserDeploy() throws Exception {
        CiCommandLineParser contextParser = new CiCommandLineParser();
        String[] clParams = ("--testDir " + tmpDir.getAbsolutePath() + " --target target --mode DEPLOY --deployDir deploydir --workflowName workflow").split(" ");

        CiCommandLine commandLine = contextParser.parse(clParams);

        Assert.assertEquals(commandLine.getDeployDir(), new File("deploydir"));
        Assert.assertEquals(commandLine.getMode(), CelosCiContext.Mode.DEPLOY);
        Assert.assertEquals(commandLine.getTargetUri(), URI.create("target"));
        Assert.assertEquals(commandLine.getUserName(), System.getProperty("user.name"));
        Assert.assertEquals(commandLine.getWorkflowName(), "workflow");
    }

    @Test
    public void testContextParserUndeploy() throws Exception {
        CiCommandLineParser contextParser = new CiCommandLineParser();

        String[] clParams = ("--testDir " + tmpDir.getAbsolutePath() + " --target target --mode UNDEPLOY --deployDir deploydir --workflowName workflow").split(" ");

        CiCommandLine commandLine = contextParser.parse(clParams);

        Assert.assertEquals(commandLine.getDeployDir(), new File("deploydir"));
        Assert.assertEquals(commandLine.getMode(), CelosCiContext.Mode.UNDEPLOY);
        Assert.assertEquals(commandLine.getTargetUri(), URI.create("target"));
        Assert.assertEquals(commandLine.getUserName(), System.getProperty("user.name"));
        Assert.assertEquals(commandLine.getWorkflowName(), "workflow");
    }


    @Test(expected = RuntimeException.class)
    public void testContextParserTestFails() throws Exception {
        CiCommandLineParser contextParser = new CiCommandLineParser();
        String[] clParams = "--target target --mode TEST --deployDir deploydir --workflowName workflow".split(" ");

        CiCommandLine commandLine = contextParser.parse(clParams);

        Assert.assertEquals(commandLine.getDeployDir(), new File("deploydir"));
        Assert.assertEquals(commandLine.getMode(), CelosCiContext.Mode.TEST);
        Assert.assertEquals(commandLine.getTargetUri(), URI.create("target"));
        Assert.assertEquals(commandLine.getUserName(), System.getProperty("user.name"));
        Assert.assertEquals(commandLine.getWorkflowName(), "workflow");
    }

    @Test
    public void testDirComesFromCLI() throws Exception {
        CiCommandLineParser contextParser = new CiCommandLineParser();

        CommandLine commandLine = mock(CommandLine.class);
        doReturn("testCasesDir").when(commandLine).getOptionValue("testDir");
        String result = contextParser.getTestCasesDir(commandLine, null);
        Assert.assertEquals(result, "testCasesDir");
    }

    @Test
    public void testDirDefault() throws Exception {
        CiCommandLineParser contextParser = new CiCommandLineParser();

        CommandLine commandLine = mock(CommandLine.class);
        String result = contextParser.getTestCasesDir(commandLine, tmpDir.getAbsolutePath());
        Assert.assertEquals(result, tmpDir.getAbsolutePath());
    }

    @Test (expected = RuntimeException.class)
    public void testNoDirComes() throws Exception {
        CiCommandLineParser contextParser = new CiCommandLineParser();

        CommandLine commandLine = mock(CommandLine.class);
        contextParser.getTestCasesDir(commandLine, "");
    }

}
