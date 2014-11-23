package com.collective.celos.ci.config.deploy;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.ContextParser;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;

/**
 * Created by akonopko on 10/1/14.
 */
public class ContextParserTest {

    @Test
    public void testContextParserTest() throws Exception {
        ContextParser contextParser = new ContextParser();
        String[] clParams = "--target target --mode TEST --deployDir deploydir --workflowName workflow".split(" ");

        CelosCiCommandLine commandLine = contextParser.parse(clParams);

        Assert.assertEquals(commandLine.getDeployDir(), new File("deploydir"));
        Assert.assertEquals(commandLine.getMode(), CelosCiContext.Mode.TEST);
        Assert.assertEquals(commandLine.getTargetUri(), URI.create("target"));
        Assert.assertEquals(commandLine.getUserName(), System.getProperty("user.name"));
        Assert.assertEquals(commandLine.getWorkflowName(), "workflow");
    }

    @Test
    public void testContextParserDeploy() throws Exception {
        ContextParser contextParser = new ContextParser();
        String[] clParams = "--target target --mode DEPLOY --deployDir deploydir --workflowName workflow".split(" ");

        CelosCiCommandLine commandLine = contextParser.parse(clParams);

        Assert.assertEquals(commandLine.getDeployDir(), new File("deploydir"));
        Assert.assertEquals(commandLine.getMode(), CelosCiContext.Mode.DEPLOY);
        Assert.assertEquals(commandLine.getTargetUri(), URI.create("target"));
        Assert.assertEquals(commandLine.getUserName(), System.getProperty("user.name"));
        Assert.assertEquals(commandLine.getWorkflowName(), "workflow");
    }

    @Test
    public void testContextParserUndeploy() throws Exception {
        ContextParser contextParser = new ContextParser();
        String[] clParams = "--target target --mode UNDEPLOY --deployDir deploydir --workflowName workflow".split(" ");

        CelosCiCommandLine commandLine = contextParser.parse(clParams);

        Assert.assertEquals(commandLine.getDeployDir(), new File("deploydir"));
        Assert.assertEquals(commandLine.getMode(), CelosCiContext.Mode.UNDEPLOY);
        Assert.assertEquals(commandLine.getTargetUri(), URI.create("target"));
        Assert.assertEquals(commandLine.getUserName(), System.getProperty("user.name"));
        Assert.assertEquals(commandLine.getWorkflowName(), "workflow");
    }

}
