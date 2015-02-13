package com.collective.celos.ci.mode.test;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.net.URI;
import java.util.Arrays;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestRunTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testCelosCiDeployContext() throws Exception {


        URI hadoopCoreUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/core-site.xml").toURI();
        URI hadoopHdfsUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/hdfs-site.xml").toURI();

        CelosCiCommandLine commandLine = new CelosCiCommandLine("", "TEST", "deploydir", "workflow", "testDir", "uname");
        CelosCiTarget target = new CelosCiTarget(hadoopHdfsUrl, hadoopCoreUrl, URI.create("celoswfdir"), URI.create("defdir"), URI.create(""));
        TestCase testCase = new TestCase("tc1", "2013-12-20T16:00Z", "2013-12-20T16:00Z");
        TestRun celosCiTest = new TestRun(target, commandLine, testCase);

        CelosCiContext context = celosCiTest.getCiContext();

        String tmpDir = new File(System.getProperty("java.io.tmpdir"), "celos").getAbsolutePath();

        Assert.assertEquals(context.getDeployDir(), commandLine.getDeployDir());
        Assert.assertTrue(StringUtils.isNotEmpty(context.getHdfsPrefix()));
        Assert.assertEquals(context.getMode(), commandLine.getMode());
        Assert.assertEquals(context.getTarget().getWorkflowsDirUri(), new File(celosCiTest.getCelosTempDir(), "workflows").toURI());
        Assert.assertFalse(context.getTarget().getDefaultsDirUri().toString().startsWith(tmpDir));
        Assert.assertEquals(context.getTarget().getPathToCoreSite(), hadoopCoreUrl);
        Assert.assertEquals(context.getTarget().getPathToHdfsSite(), hadoopHdfsUrl);
        Assert.assertEquals(context.getUserName(), commandLine.getUserName());
        Assert.assertEquals(context.getWorkflowName(), commandLine.getWorkflowName());

        Assert.assertTrue(celosCiTest.getCelosTempDir().toString().startsWith(tmpDir));
        Assert.assertTrue(celosCiTest.getCelosTempDir().toString().length() > tmpDir.length());
        Assert.assertEquals(celosCiTest.getCelosDbDir(), new File(celosCiTest.getCelosTempDir(), "db"));
        Assert.assertEquals(celosCiTest.getCelosWorkflowDir().toURI(), context.getTarget().getWorkflowsDirUri());
    }

    @Test
    public void testCopyRemoteDefaults() throws Exception {

        URI hadoopCoreUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/core-site.xml").toURI();
        URI hadoopHdfsUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/hdfs-site.xml").toURI();

        File deployDir = tempDir.newFolder();
        deployDir.mkdirs();
        File defFile = new File(deployDir, "defaults.js");
        defFile.createNewFile();

        File remoteFolderWf = tempDir.newFolder();
        remoteFolderWf.mkdirs();

        File remoteFolderDef = tempDir.newFolder();
        remoteFolderDef.mkdirs();
        File otherDefFile1 = new File(remoteFolderDef, "some-defaults1.js");
        otherDefFile1.createNewFile();
        File otherDefFile2 = new File(remoteFolderDef, "some-defaults2.js");
        otherDefFile2.createNewFile();

        CelosCiCommandLine commandLine = new CelosCiCommandLine("", "TEST", deployDir.getAbsolutePath(), "myworkflow", "testDir", "uname");
        CelosCiTarget target = new CelosCiTarget(hadoopHdfsUrl, hadoopCoreUrl, remoteFolderWf.toURI(), remoteFolderDef.toURI(), URI.create(""));
        TestCase testCase = new TestCase("tc1", "2013-12-20T16:00Z", "2013-12-20T16:00Z");

        TestRun testRun = new TestRun(target, commandLine, testCase);

        testRun.prepareCelosServerEnv();

        String[] fileNames = testRun.getCelosDefaultsDir().list();
        Arrays.sort(fileNames);

        Assert.assertArrayEquals(fileNames, new String[] {"some-defaults1.js", "some-defaults2.js"});

    }


    @Test
    public void testCopyRemoteDefaultsNoTargetDefaults() throws Exception {

        URI hadoopCoreUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/core-site.xml").toURI();
        URI hadoopHdfsUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/hdfs-site.xml").toURI();

        File deployDir = tempDir.newFolder();
        deployDir.mkdirs();
        File defFile = new File(deployDir, "defaults.js");
        defFile.createNewFile();

        CelosCiCommandLine commandLine = new CelosCiCommandLine("", "TEST", deployDir.getAbsolutePath(), "myworkflow", "testDir", "uname");
        CelosCiTarget target = new CelosCiTarget(hadoopHdfsUrl, hadoopCoreUrl, null, null, URI.create(""));
        TestCase testCase = new TestCase("tc1", "2013-12-20T16:00Z", "2013-12-20T16:00Z");

        TestRun testRun = new TestRun(target, commandLine, testCase);

        testRun.prepareCelosServerEnv();

        String[] fileNames = testRun.getCelosDefaultsDir().list();

        Assert.assertArrayEquals(fileNames, new String[] {});

    }

}