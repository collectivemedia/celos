package com.collective.celos.ci.mode;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.powermock.api.mockito.PowerMockito;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.net.URI;

/**
 * Created by akonopko on 10/1/14.
 */
public class UndeployTaskTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testCelosCiUndeployContext() throws Exception {


        String hadoopCoreUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/core-site.xml").getFile();
        String hadoopHdfsUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/hdfs-site.xml").getFile();

        String targetFileStr = "{\n" +
                "    \"security.settings\": \"secsettings\",\n" +
                "    \"workflows.dir.uri\": \"celoswfdir\",\n" +
                "    \"hadoop.hdfs-site.xml\": \"" + hadoopHdfsUrl +"\",\n" +
                "    \"hadoop.core-site.xml\": \"" + hadoopCoreUrl +"\",\n" +
                "    \"defaults.dir.uri\": \"defdir\"\n" +
                "}\n";

        File targetFile = tempDir.newFile();
        FileOutputStream stream = new FileOutputStream(targetFile);
        stream.write(targetFileStr.getBytes());
        stream.flush();

        CelosCiCommandLine commandLine = new CelosCiCommandLine(targetFile.toURI().toString(), "UNDEPLOY", "deploydir", "workflow", "testDir", "uname");
        UndeployTask celosCiDeploy = new UndeployTask(commandLine);

        CelosCiContext context = celosCiDeploy.getCiContext();

        Assert.assertEquals(context.getDeployDir(), commandLine.getDeployDir());
        Assert.assertEquals(context.getHdfsPrefix(), "");
        Assert.assertEquals(context.getMode(), commandLine.getMode());
        Assert.assertEquals(context.getTarget().getWorkflowsDirUri(), URI.create("celoswfdir"));
        Assert.assertEquals(context.getTarget().getDefaultsDirUri(), URI.create("defdir"));
        Assert.assertEquals(context.getTarget().getPathToCoreSite(), URI.create(hadoopCoreUrl));
        Assert.assertEquals(context.getTarget().getPathToHdfsSite(), URI.create(hadoopHdfsUrl));
        Assert.assertEquals(context.getUserName(), commandLine.getUserName());
        Assert.assertEquals(context.getWorkflowName(), commandLine.getWorkflowName());
    }

    @Test
    public void testCelosCiUndeployStart() throws Exception {


        String hadoopCoreUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/core-site.xml").getFile();
        String hadoopHdfsUrl = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/hdfs-site.xml").getFile();

        File celosWfDir = tempDir.newFolder();
        File hdfsDir = tempDir.newFolder();
        File deployDir = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/deploy").getFile());

        String targetFileStr = "{\n" +
                "    \"workflows.dir.uri\": \"" + celosWfDir.getAbsolutePath() +"\",\n" +
                "    \"hadoop.hdfs-site.xml\": \"" + hadoopHdfsUrl +"\",\n" +
                "    \"hadoop.core-site.xml\": \"" + hadoopCoreUrl +"\"\n" +
                "}\n";

        File targetFile = tempDir.newFile();
        FileOutputStream stream = new FileOutputStream(targetFile);
        stream.write(targetFileStr.getBytes());
        stream.flush();

        CelosCiCommandLine commandLine = new CelosCiCommandLine(targetFile.toURI().toString(), "UNDEPLOY", deployDir.getAbsolutePath(), "workflow", "testDir", "uname");

        UndeployTask undeployTask = new UndeployTask(commandLine);

        Field f = PowerMockito.field(CelosCiContext.class, "hdfsPrefix");
        f.set(undeployTask.getCiContext(), hdfsDir.getAbsolutePath());

        File workflowFile = new File(celosWfDir, "workflow.js");
        workflowFile.createNewFile();

        File hdfsDirFullPath = new File(hdfsDir, "user/celos/app/workflow");
        hdfsDirFullPath.mkdirs();
        new File(hdfsDirFullPath, "file1").createNewFile();
        new File(hdfsDirFullPath, "file2").createNewFile();

        Assert.assertTrue(workflowFile.exists());
        Assert.assertEquals(Sets.newHashSet("file1", "file2"), Sets.newHashSet(hdfsDirFullPath.list()));

        undeployTask.start();

        Assert.assertFalse(workflowFile.exists());
        Assert.assertFalse(hdfsDirFullPath.exists());
    }

}
