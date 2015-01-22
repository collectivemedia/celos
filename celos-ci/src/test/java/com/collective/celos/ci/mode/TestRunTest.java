package com.collective.celos.ci.mode;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import com.collective.celos.ci.mode.test.TestCase;
import com.collective.celos.ci.mode.test.TestRun;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.io.File;
import java.net.URI;

/**
 * Created by akonopko on 10/1/14.
 */
public class TestRunTest {

    @Test
    public void testCelosCiDeployContext() throws Exception {


        URI hadoopCoreUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/core-site.xml").toURI();
        URI hadoopHdfsUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/hdfs-site.xml").toURI();

        CelosCiCommandLine commandLine = new CelosCiCommandLine("", "TEST", "deploydir", "workflow", "testDir", "uname");
        CelosCiTarget target = new CelosCiTarget(hadoopHdfsUrl, hadoopCoreUrl, URI.create("celoswfdir"), URI.create("deffile"), URI.create(""));
        TestCase testCase = new TestCase("tc1", "2013-12-20T16:00Z", "2013-12-20T16:00Z");
        TestRun celosCiTest = new TestRun(target, commandLine, testCase);

        CelosCiContext context = celosCiTest.getCiContext();

        Assert.assertEquals(context.getDeployDir(), commandLine.getDeployDir());
        Assert.assertTrue(StringUtils.isNotEmpty(context.getHdfsPrefix()));
        Assert.assertEquals(context.getMode(), commandLine.getMode());
        Assert.assertEquals(context.getTarget().getCelosWorkflowsDirUri(), new File(celosCiTest.getCelosTempDir(), "workflows").toURI());
        Assert.assertEquals(context.getTarget().getDefaultsFile(), URI.create("deffile"));
        Assert.assertEquals(context.getTarget().getPathToCoreSite(), hadoopCoreUrl);
        Assert.assertEquals(context.getTarget().getPathToHdfsSite(), hadoopHdfsUrl);
        Assert.assertEquals(context.getUserName(), commandLine.getUserName());
        Assert.assertEquals(context.getWorkflowName(), commandLine.getWorkflowName());

        String tmpDir = new File(System.getProperty("java.io.tmpdir"), "celos").getAbsolutePath();
        Assert.assertTrue(celosCiTest.getCelosTempDir().toString().startsWith(tmpDir));
        Assert.assertTrue(celosCiTest.getCelosTempDir().toString().length() > tmpDir.length());
        Assert.assertEquals(celosCiTest.getCelosDbDir(), new File(celosCiTest.getCelosTempDir(), "db"));
        Assert.assertEquals(celosCiTest.getCelosWorkflowDir().toURI(), context.getTarget().getCelosWorkflowsDirUri());
    }

}
