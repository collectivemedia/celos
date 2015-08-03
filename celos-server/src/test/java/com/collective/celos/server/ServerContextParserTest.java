package com.collective.celos.server;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import com.collective.celos.Constants;

public class ServerContextParserTest {
    
    @Test(expected=IllegalArgumentException.class)
    public void testPortIsRequired() throws Exception {
        ServerCommandLine cmdLine = new ServerContextParser().parse(new String[] {});
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testPortIsRequiredEvenIfOtherArgsAreSupplied() throws Exception {
        ServerCommandLine cmdLine = new ServerContextParser().parse(new String[] {"--defaultsDir", "/foo"});
    }
    
    @Test
    public void testDefaults() throws Exception {
        ServerCommandLine cmdLine = new ServerContextParser().parse(new String[] {"--port", "1337"});
        Assert.assertEquals((Integer) 1337, cmdLine.getPort());
        Assert.assertEquals(new File(Constants.DEFAULT_WORKFLOWS_DIR), cmdLine.getWorkflowsDir());
        Assert.assertEquals(new File(Constants.DEFAULT_DEFAULTS_DIR), cmdLine.getDefaultsDir());
        Assert.assertEquals(new File(Constants.DEFAULT_DB_DIR), cmdLine.getStateDatabase());
        Assert.assertEquals(new File(Constants.DEFAULT_UI_DIR), cmdLine.getUiDir());
    }
    
    @Test
    public void testOverrideDefaults() throws Exception {
        ServerCommandLine cmdLine = new ServerContextParser().parse(new String[] {"--port", "1337", "--workflowsDir", "/wf", "--stateDir", "/db",  "--uiDir", "/ui", "--defaultsDir", "/defaults"});
        Assert.assertEquals((Integer) 1337, cmdLine.getPort());
        Assert.assertEquals(new File("/wf"), cmdLine.getWorkflowsDir());
        Assert.assertEquals(new File("/defaults"), cmdLine.getDefaultsDir());
        Assert.assertEquals(new File("/db"), cmdLine.getStateDatabase());
        Assert.assertEquals(new File("/ui"), cmdLine.getUiDir());
    }

}
