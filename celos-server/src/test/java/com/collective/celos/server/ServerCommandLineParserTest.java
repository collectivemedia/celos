/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
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
        Assert.assertNull(cmdLine.getDigestConfig());
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
