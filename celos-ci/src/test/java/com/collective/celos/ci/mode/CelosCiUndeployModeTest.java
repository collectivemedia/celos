package com.collective.celos.ci.mode;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

/**
 * Created by akonopko on 10/1/14.
 */
public class CelosCiUndeployModeTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testCelosCiUndeployContext() throws Exception {


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

        CelosCiCommandLine commandLine = new CelosCiCommandLine(targetFile.toURI().toString(), "UNDEPLOY", "deploydir", "workflow", "testDir", "uname");
        CelosCiUndeploy celosCiDeploy = new CelosCiUndeploy(commandLine);

        CelosCiContext context = celosCiDeploy.getCiContext();

        Assert.assertEquals(context.getDeployDir(), commandLine.getDeployDir());
        Assert.assertEquals(context.getHdfsPrefix(), "");
        Assert.assertEquals(context.getMode(), commandLine.getMode());
        Assert.assertEquals(context.getTarget().getCelosWorkflowsDirUri(), URI.create("celoswfdir"));
        Assert.assertEquals(context.getTarget().getDefaultsFile(), URI.create("deffile"));
        Assert.assertEquals(context.getTarget().getPathToCoreSite(), URI.create(hadoopCoreUrl));
        Assert.assertEquals(context.getTarget().getPathToHdfsSite(), URI.create(hadoopHdfsUrl));
        Assert.assertEquals(context.getTarget().getScpSecuritySettings(), "secsettings");
        Assert.assertEquals(context.getUserName(), commandLine.getUserName());
        Assert.assertEquals(context.getWorkflowName(), commandLine.getWorkflowName());
    }
}
