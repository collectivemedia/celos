package com.collective.celos.ci.mode;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.config.deploy.CelosCiTarget;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
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
        CelosCiTarget target = new CelosCiTarget(hadoopHdfsUrl, hadoopCoreUrl, URI.create("celoswfdir"), URI.create("deffile"));
        TestRun celosCiTest = new TestRun(target, commandLine.getUserName(), commandLine.getWorkflowName(), commandLine.getDeployDir(), commandLine.getTestCasesDir());

        CelosCiContext context = celosCiTest.getCiContext();

        Assert.assertEquals(context.getDeployDir(), commandLine.getDeployDir());
        Assert.assertTrue(StringUtils.isNotEmpty(context.getHdfsPrefix()));
        Assert.assertEquals(context.getMode(), commandLine.getMode());
        Assert.assertEquals(context.getTarget().getCelosWorkflowsDirUri(), new File(celosCiTest.getTestContext().getCelosWorkDir(), "workflows").toURI());
        Assert.assertEquals(context.getTarget().getDefaultsFile(), URI.create("deffile"));
        Assert.assertEquals(context.getTarget().getPathToCoreSite(), hadoopCoreUrl);
        Assert.assertEquals(context.getTarget().getPathToHdfsSite(), hadoopHdfsUrl);
        Assert.assertEquals(context.getUserName(), commandLine.getUserName());
        Assert.assertEquals(context.getWorkflowName(), commandLine.getWorkflowName());

        Assert.assertTrue(celosCiTest.getTestContext().getCelosWorkDir().toString().startsWith("/tmp/celos"));
        Assert.assertTrue(celosCiTest.getTestContext().getCelosWorkDir().toString().length() > "/tmp/celos".length());
        Assert.assertEquals(celosCiTest.getTestContext().getCelosDbDir(), new File(celosCiTest.getTestContext().getCelosWorkDir(), "db"));
        Assert.assertEquals(celosCiTest.getTestContext().getCelosWorkflowDir().toURI(), context.getTarget().getCelosWorkflowsDirUri());
    }

}