package com.collective.celos.ci.mode;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
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
public class CelosCiTestModeTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testCelosCiDeployContext() throws Exception {


        String hadoopCoreUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/core-site.xml").getFile();
        String hadoopHdfsUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/hdfs-site.xml").getFile();

        String targetFileStr = "{\n" +
                "    \"security.settings\": \"secsettings\",\n" +
                "    \"celos.workflow.dir\": \"celoswfdir\",\n" +
                "    \"hadoop.hdfs-site.xml\": \"" + hadoopHdfsUrl +"\",\n" +
                "    \"hadoop.core-site.xml\": \"" + hadoopCoreUrl +"\",\n" +
                "    \"defaults.file.uri\": \"deffile\"\n" +
                "}\n";

        File targetFile = tempDir.newFile();
        FileOutputStream stream = new FileOutputStream(targetFile);
        stream.write(targetFileStr.getBytes());
        stream.flush();

        CelosCiCommandLine commandLine = new CelosCiCommandLine(targetFile.toURI().toString(), "TEST", "deploydir", "workflow", "testDir", "uname");
        TestTask celosCiTest = new TestTask(commandLine);

        CelosCiContext context = celosCiTest.getCiContext();

        Assert.assertEquals(context.getDeployDir(), commandLine.getDeployDir());
        Assert.assertTrue(StringUtils.isNotEmpty(context.getHdfsPrefix()));
        Assert.assertEquals(context.getMode(), commandLine.getMode());
        Assert.assertEquals(context.getTarget().getCelosWorkflowsDirUri(), new File(celosCiTest.getTestContext().getCelosWorkDir(), "workflows").toURI());
        Assert.assertEquals(context.getTarget().getDefaultsFile(), URI.create("deffile"));
        Assert.assertEquals(context.getTarget().getPathToCoreSite(), URI.create(hadoopCoreUrl));
        Assert.assertEquals(context.getTarget().getPathToHdfsSite(), URI.create(hadoopHdfsUrl));
        Assert.assertEquals(context.getUserName(), commandLine.getUserName());
        Assert.assertEquals(context.getWorkflowName(), commandLine.getWorkflowName());

        Assert.assertTrue(celosCiTest.getTestContext().getCelosWorkDir().toString().startsWith("/tmp/celos"));
        Assert.assertTrue(celosCiTest.getTestContext().getCelosWorkDir().toString().length() > "/tmp/celos".length());
        Assert.assertEquals(celosCiTest.getTestContext().getCelosDbDir(), new File(celosCiTest.getTestContext().getCelosWorkDir(), "db"));
        Assert.assertEquals(celosCiTest.getTestContext().getCelosWorkflowDir().toURI(), context.getTarget().getCelosWorkflowsDirUri());
    }

}
