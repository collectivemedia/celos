package com.collective.celos.ci.mode.test;

import com.collective.celos.ci.config.CiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
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
public class TestRunTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    public File tmpDirFile;

    @Before
    public void before() throws IOException {
        tmpDirFile = tempDir.newFolder();
    }


    @Test
    public void testCelosCiDeployContextEmbedded() throws Exception {


        URI hadoopCoreUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/core-site.xml").toURI();
        URI hadoopHdfsUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/hdfs-site.xml").toURI();

        CiCommandLine commandLine = new CiCommandLine("", "TEST", tmpDirFile.getAbsolutePath(), "workflow", tmpDirFile.getAbsolutePath(), "uname", false, null, "/hdfsRoot");
        CelosCiTarget target = new CelosCiTarget(hadoopHdfsUrl, hadoopCoreUrl, URI.create("celoswfdir"), URI.create("defdir"), URI.create(""));
        TestCase testCase = new TestCase("tc1", "2013-12-20T16:00Z", "2013-12-20T16:00Z");
        TestRun testRunContext = new TestRun(target, commandLine, testCase, tempDir.newFolder());

        CelosCiContext context = testRunContext.getCiContext();

        String tmpDir = new File(System.getProperty("java.io.tmpdir"), "celos").getAbsolutePath();

        Assert.assertEquals(context.getDeployDir(), commandLine.getDeployDir());
        Assert.assertTrue(StringUtils.isNotEmpty(context.getHdfsPrefix()));
        Assert.assertEquals(context.getMode(), commandLine.getMode());
        Assert.assertEquals(context.getTarget().getWorkflowsDirUri(), new File(testRunContext.getTestCaseTempDir(), "workflows").toURI());
        Assert.assertFalse(context.getTarget().getDefaultsDirUri().toString().startsWith(tmpDir));
        Assert.assertEquals(context.getTarget().getPathToCoreSite(), hadoopCoreUrl);
        Assert.assertEquals(context.getTarget().getPathToHdfsSite(), hadoopHdfsUrl);
        Assert.assertEquals(context.getUserName(), commandLine.getUserName());
        Assert.assertEquals(context.getWorkflowName(), commandLine.getWorkflowName());

        Assert.assertTrue(testRunContext.getTestCaseTempDir().toString().startsWith(System.getProperty("java.io.tmpdir")));
        Assert.assertTrue(testRunContext.getTestCaseTempDir().toString().length() > tmpDir.length());
        Assert.assertEquals(testRunContext.getOriginalTarget().getWorkflowsDirUri(), URI.create("celoswfdir"));
    }


    @Test
    public void testCelosCiDeployContextProvided() throws Exception {


        URI hadoopCoreUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/core-site.xml").toURI();
        URI hadoopHdfsUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/hdfs-site.xml").toURI();

        CiCommandLine commandLine = new CiCommandLine("", "TEST", tmpDirFile.getAbsolutePath(), "workflow", tmpDirFile.getAbsolutePath(), "uname", false, "http://localhost:1234", "/hdfsRoot");
        CelosCiTarget target = new CelosCiTarget(hadoopHdfsUrl, hadoopCoreUrl, URI.create("celoswfdir"), URI.create("defdir"), URI.create(""));
        TestCase testCase = new TestCase("tc1", "2013-12-20T16:00Z", "2013-12-20T16:00Z");
        TestRun testRunContext = new TestRun(target, commandLine, testCase, tempDir.newFolder());

        CelosCiContext context = testRunContext.getCiContext();

        String tmpDir = new File(System.getProperty("java.io.tmpdir"), "celos").getAbsolutePath();

        Assert.assertEquals(context.getDeployDir(), commandLine.getDeployDir());
        Assert.assertTrue(StringUtils.isNotEmpty(context.getHdfsPrefix()));
        Assert.assertEquals(context.getMode(), commandLine.getMode());
        Assert.assertEquals(context.getTarget().getWorkflowsDirUri(), URI.create("celoswfdir"));
        Assert.assertFalse(context.getTarget().getDefaultsDirUri().toString().startsWith(tmpDir));
        Assert.assertEquals(context.getTarget().getPathToCoreSite(), hadoopCoreUrl);
        Assert.assertEquals(context.getTarget().getPathToHdfsSite(), hadoopHdfsUrl);
        Assert.assertEquals(context.getUserName(), commandLine.getUserName());
        Assert.assertEquals(context.getWorkflowName(), commandLine.getWorkflowName());

        Assert.assertTrue(testRunContext.getTestCaseTempDir().toString().startsWith(System.getProperty("java.io.tmpdir")));
        Assert.assertTrue(testRunContext.getTestCaseTempDir().toString().length() > tmpDir.length());
        Assert.assertEquals(testRunContext.getOriginalTarget().getWorkflowsDirUri(), URI.create("celoswfdir"));
    }

}
