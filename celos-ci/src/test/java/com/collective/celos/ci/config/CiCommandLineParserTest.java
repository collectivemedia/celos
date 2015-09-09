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

        String[] clParams = ("--testDir " + tmpDir.getAbsolutePath() + " --target target --mode TEST --deployDir "  + tmpDir.getAbsolutePath() + "  --workflowName workflow").split(" ");

        CiCommandLine commandLine = contextParser.parse(clParams);

        Assert.assertEquals(commandLine.getDeployDir(), tmpDir);
        Assert.assertEquals(commandLine.getMode(), CelosCiContext.Mode.TEST);
        Assert.assertEquals(commandLine.getTargetUri(), URI.create("target"));
        Assert.assertEquals(commandLine.getUserName(), getUsername());
        Assert.assertEquals(commandLine.getWorkflowName(), "workflow");
    }

    @Test
    public void testContextParserTestSupportLowercase() throws Exception {
        CiCommandLineParser contextParser = new CiCommandLineParser();

        String[] clParams = ("--testDir " + tmpDir.getAbsolutePath() + " --target target --mode test --deployDir " + tmpDir.getAbsolutePath() + " --workflowName workflow").split(" ");

        CiCommandLine commandLine = contextParser.parse(clParams);

        Assert.assertEquals(commandLine.getDeployDir(), tmpDir);
        Assert.assertEquals(commandLine.getMode(), CelosCiContext.Mode.TEST);
        Assert.assertEquals(commandLine.getTargetUri(), URI.create("target"));
        Assert.assertEquals(commandLine.getUserName(), getUsername());
        Assert.assertEquals(commandLine.getWorkflowName(), "workflow");
    }

    private String getUsername() {
        String userName = System.getenv(CiCommandLineParser.USERNAME_ENV_VAR);
        if (userName == null) {
            userName = System.getProperty("user.name");
        }
        return userName;
    }

    @Test
    public void testContextParserDeploy() throws Exception {
        CiCommandLineParser contextParser = new CiCommandLineParser();
        String[] clParams = ("--target target --mode DEPLOY --deployDir " + tmpDir.getAbsolutePath() + " --workflowName workflow").split(" ");

        CiCommandLine commandLine = contextParser.parse(clParams);

        Assert.assertEquals(commandLine.getDeployDir(), tmpDir);
        Assert.assertEquals(commandLine.getMode(), CelosCiContext.Mode.DEPLOY);
        Assert.assertEquals(commandLine.getTargetUri(), URI.create("target"));
        Assert.assertEquals(commandLine.getUserName(), getUsername());
        Assert.assertEquals(commandLine.getWorkflowName(), "workflow");
    }

    @Test
    public void testContextParserUndeploy() throws Exception {
        CiCommandLineParser contextParser = new CiCommandLineParser();

        String[] clParams = ("--target target --mode UNDEPLOY --workflowName workflow").split(" ");

        CiCommandLine commandLine = contextParser.parse(clParams);

        Assert.assertNull(commandLine.getDeployDir());
        Assert.assertEquals(commandLine.getMode(), CelosCiContext.Mode.UNDEPLOY);
        Assert.assertEquals(commandLine.getTargetUri(), URI.create("target"));
        Assert.assertEquals(commandLine.getUserName(), getUsername());
        Assert.assertEquals(commandLine.getWorkflowName(), "workflow");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testContextParserTestFailsNoTestDir() throws Exception {
        CiCommandLineParser contextParser = new CiCommandLineParser();
        String[] clParams = ("--target target --mode TEST --deployDir " + tmpDir.getAbsolutePath() + " --workflowName workflow").split(" ");

        contextParser.parse(clParams);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNoDirComes() throws Exception {
        new CiCommandLine("target", "TEST", tmpDir.getAbsolutePath(), "workflowName", "testCasesDir", "userName", true, null, "/hdfsRoot");
    }

    @Test (expected = NullPointerException.class)
    public void testNoDirComesNull() throws Exception {
        new CiCommandLine("target", "TEST", tmpDir.getAbsolutePath(), "workflowName", null, "userName", true, null, "/hdfsRoot");
    }

    @Test
    public void testDeployNoDirComes() throws Exception {
        new CiCommandLine("target", "DEPLOY", tmpDir.getAbsolutePath(), "workflowName", "testCasesDir", "userName", true, null, "/hdfsRoot");
    }

    @Test
    public void testDeployNoDirComesNull() throws Exception {
        new CiCommandLine("target", "DEPLOY", tmpDir.getAbsolutePath(), "workflowName", null, "userName", true, null, "/hdfsRoot");
    }

    @Test
    public void testUndeployNoDirComes() throws Exception {
        new CiCommandLine("target", "UNDEPLOY", tmpDir.getAbsolutePath(), "workflowName", "testCasesDir", "userName", true, null, "/hdfsRoot");
    }

    @Test
    public void testUndeployNoDirComesNull() throws Exception {
        new CiCommandLine("target", "UNDEPLOY", tmpDir.getAbsolutePath(), "workflowName", null, "userName", true, null, "/hdfsRoot");
    }

    @Test
    public void testHdfsRootSeveralSlashesInMiddle() throws Exception {
        new CiCommandLine("target", "UNDEPLOY", tmpDir.getAbsolutePath(), "workflowName", null, "userName", true, null, "/hdfs/Some/Root");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHdfsRootNoStartSlash() throws Exception {
        new CiCommandLine("target", "UNDEPLOY", tmpDir.getAbsolutePath(), "workflowName", null, "userName", true, null, "hdfsRoot");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHdfsRootEndsWithSlash() throws Exception {
        new CiCommandLine("target", "UNDEPLOY", tmpDir.getAbsolutePath(), "workflowName", null, "userName", true, null, "/hdfsRoot/");
    }


}
