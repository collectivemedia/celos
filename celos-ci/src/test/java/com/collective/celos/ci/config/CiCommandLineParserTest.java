package com.collective.celos.ci.config;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URI;

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
        String[] clParams = ("--target target --mode DEPLOY --deployDir deploydir --workflowName workflow").split(" ");

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

        String[] clParams = ("--target target --mode UNDEPLOY --deployDir deploydir --workflowName workflow").split(" ");

        CiCommandLine commandLine = contextParser.parse(clParams);

        Assert.assertEquals(commandLine.getDeployDir(), new File("deploydir"));
        Assert.assertEquals(commandLine.getMode(), CelosCiContext.Mode.UNDEPLOY);
        Assert.assertEquals(commandLine.getTargetUri(), URI.create("target"));
        Assert.assertEquals(commandLine.getUserName(), System.getProperty("user.name"));
        Assert.assertEquals(commandLine.getWorkflowName(), "workflow");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testContextParserTestFailsNoTestDir() throws Exception {
        CiCommandLineParser contextParser = new CiCommandLineParser();
        String[] clParams = "--target target --mode TEST --deployDir deploydir --workflowName workflow".split(" ");

        contextParser.parse(clParams);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNoDirComes() throws Exception {
        new CiCommandLine("target", "TEST", "deployDir", "workflowName", "testCasesDir", "userName", true, null);
    }

    @Test (expected = NullPointerException.class)
    public void testNoDirComesNull() throws Exception {
        new CiCommandLine("target", "TEST", "deployDir", "workflowName", null, "userName", true, null);
    }

    @Test
    public void testDeployNoDirComes() throws Exception {
        new CiCommandLine("target", "DEPLOY", "deployDir", "workflowName", "testCasesDir", "userName", true, null);
    }

    @Test
    public void testDeployNoDirComesNull() throws Exception {
        new CiCommandLine("target", "DEPLOY", "deployDir", "workflowName", null, "userName", true, null);
    }

    @Test
    public void testUndeployNoDirComes() throws Exception {
        new CiCommandLine("target", "UNDEPLOY", "deployDir", "workflowName", "testCasesDir", "userName", true, null);
    }

    @Test
    public void testUndeployNoDirComesNull() throws Exception {
        new CiCommandLine("target", "UNDEPLOY", "deployDir", "workflowName", null, "userName", true, null);
    }

}
