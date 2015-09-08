package com.collective.celos.server;

import com.collective.celos.Constants;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;

public class ServerCommandLineParserTest {
    
    @Test(expected=IllegalArgumentException.class)
    public void testPortIsRequired() throws Exception {
        new ServerCommandLineParser().parse(new String[] {});
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testPortIsRequiredEvenIfOtherArgsAreSupplied() throws Exception {
        new ServerCommandLineParser().parse(new String[] {"--defaults", "/foo"});
    }
    
    @Test
    public void testDefaults() throws Exception {
        ServerCommandLine cmdLine = new ServerCommandLineParser().parse(new String[] {"--port", "1337"});
        Assert.assertEquals(1337, cmdLine.getPort());
        Assert.assertEquals(new File(Constants.DEFAULT_WORKFLOWS_DIR), cmdLine.getWorkflowsDir());
        Assert.assertEquals(new File(Constants.DEFAULT_DEFAULTS_DIR), cmdLine.getDefaultsDir());
        Assert.assertEquals(new File(Constants.DEFAULT_DB_DIR), cmdLine.getStateDatabase());
    }
    
    @Test
    public void testOverrideDefaults() throws Exception {
        ServerCommandLine cmdLine = new ServerCommandLineParser().parse(new String[] {"--port", "1337", "--workflows", "/wf", "--db", "/db", "--defaults", "/defaults"});
        Assert.assertEquals(1337, cmdLine.getPort());
        Assert.assertEquals(new File("/wf"), cmdLine.getWorkflowsDir());
        Assert.assertEquals(new File("/defaults"), cmdLine.getDefaultsDir());
        Assert.assertEquals(new File("/db"), cmdLine.getStateDatabase());
    }

}
