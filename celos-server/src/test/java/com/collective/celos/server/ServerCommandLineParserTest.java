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
import com.collective.celos.database.FileSystemStateDatabase;
import com.collective.celos.database.JDBCStateDatabase;
import com.collective.celos.database.StateDatabase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class ServerCommandLineParserTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File tmpDir;

    @Before
    public void setUp() throws IOException {
        tmpDir = folder.newFolder();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPortIsRequired() throws Exception {
        new ServerCommandLineParser().parse(new String[] {});
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testPortIsRequiredEvenIfOtherArgsAreSupplied() throws Exception {
        new ServerCommandLineParser().parse(new String[] {"--defaults", "/foo"});
    }

    @Test (expected = IOException.class)
    public void testDefaultsNoDb() throws Exception {
        new ServerCommandLineParser().parse(new String[] {"--port", "1337" });
    }

    @Test
    public void testDefaults() throws Exception {
        ServerCommandLine cmdLine = new ServerCommandLineParser().parse(new String[] {"--port", "1337", "--db", tmpDir.getAbsolutePath() });
        Assert.assertEquals(1337, cmdLine.getPort());
        Assert.assertEquals(new File(Constants.DEFAULT_WORKFLOWS_DIR), cmdLine.getWorkflowsDir());
        Assert.assertEquals(new File(Constants.DEFAULT_DEFAULTS_DIR), cmdLine.getDefaultsDir());
        Assert.assertEquals(StateDatabase.DatabaseType.FILESYSTEM, cmdLine.getDatabase().getDatabaseType());
        FileSystemStateDatabase db = (FileSystemStateDatabase) cmdLine.getDatabase();
        Assert.assertEquals(db.getDir(), tmpDir);
    }

    @Test
    public void testOverrideDefaults() throws Exception {
        ServerCommandLine cmdLine = new ServerCommandLineParser().parse(new String[] {"--port", "1337", "--workflows", "/wf", "--db", tmpDir.getAbsolutePath(), "--defaults", "/defaults"});
        Assert.assertEquals(1337, cmdLine.getPort());
        Assert.assertEquals(new File("/wf"), cmdLine.getWorkflowsDir());
        Assert.assertEquals(new File("/defaults"), cmdLine.getDefaultsDir());
        Assert.assertEquals(StateDatabase.DatabaseType.FILESYSTEM, cmdLine.getDatabase().getDatabaseType());
        FileSystemStateDatabase db = (FileSystemStateDatabase) cmdLine.getDatabase();
        Assert.assertEquals(tmpDir, db.getDir());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testJdbcConnectionParamsNoUrl() throws Exception {
        new ServerCommandLineParser().parse(new String[]{"--port", "1337", "--workflows", "/wf", "--dbType", "jdbc", "--defaults", "/defaults"});
    }

    @Test (expected = IllegalArgumentException.class)
    public void testJdbcConnectionParamsNoUsername() throws Exception {
        new ServerCommandLineParser().parse(new String[] {"--port", "1337", "--workflows", "/wf", "--dbType", "jdbc" , "--jdbcUrl", "url", "--jdbcName", "uname", "--defaults", "/defaults"});
    }

    @Test (expected = IllegalArgumentException.class)
    public void testJdbcConnectionParamsNoPassword() throws Exception {
        ServerCommandLine cmdLine = new ServerCommandLineParser().parse(new String[]{"--port", "1337", "--workflows", "/wf", "--dbType", "jdbc", "--jdbcUrl", "url", "--jdbcName", "uname", "--defaults", "/defaults"});
        Assert.assertEquals(1337, cmdLine.getPort());
        Assert.assertEquals(new File("/wf"), cmdLine.getWorkflowsDir());
        Assert.assertEquals(new File("/defaults"), cmdLine.getDefaultsDir());
        Assert.assertEquals(StateDatabase.DatabaseType.FILESYSTEM, cmdLine.getDatabase().getDatabaseType());
        FileSystemStateDatabase config = (FileSystemStateDatabase) cmdLine.getDatabase();
        Assert.assertEquals(new File("/db"), config.getDir());

    }

    @Test
    public void testJdbcConnectionParams() throws Exception {
        ServerCommandLine cmdLine = new ServerCommandLineParser().parse(new String[]{"--port", "1337", "--workflows", "/wf", "--dbType", "jdbc", "--jdbcUrl", "url", "--jdbcName", "uname", "--jdbcPassword", "pwd", "--defaults", "/defaults"});
        Assert.assertEquals(1337, cmdLine.getPort());
        Assert.assertEquals(new File("/wf"), cmdLine.getWorkflowsDir());
        Assert.assertEquals(new File("/defaults"), cmdLine.getDefaultsDir());
        Assert.assertEquals(StateDatabase.DatabaseType.JDBC, cmdLine.getDatabase().getDatabaseType());
        JDBCStateDatabase config = (JDBCStateDatabase) cmdLine.getDatabase();
        Assert.assertEquals("url", config.getUrl());
        Assert.assertEquals("uname", config.getName());
        Assert.assertEquals("pwd", config.getPassword());

    }

}
