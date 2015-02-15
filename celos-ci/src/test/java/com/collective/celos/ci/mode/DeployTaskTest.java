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
import java.util.Set;

/**
 * Created by akonopko on 10/1/14.
 */
public class DeployTaskTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testCelosCiDeployContext() throws Exception {


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

        CelosCiCommandLine commandLine = new CelosCiCommandLine(targetFile.toURI().toString(), "DEPLOY", "deploydir", "workflow", "testDir", "uname");
        DeployTask deployTask = new DeployTask(commandLine);

        CelosCiContext context = deployTask.getCiContext();

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
    public void testCelosCiDeployStart() throws Exception {


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

        CelosCiCommandLine commandLine = new CelosCiCommandLine(targetFile.toURI().toString(), "DEPLOY", deployDir.getAbsolutePath(), "workflow", "testDir", "uname");

        DeployTask deployTask = new DeployTask(commandLine);

        Field f = PowerMockito.field(CelosCiContext.class, "hdfsPrefix");
        f.set(deployTask.getCiContext(), hdfsDir.getAbsolutePath());

        deployTask.start();
        Assert.assertArrayEquals(celosWfDir.list(), new String[] {"workflow.js"});
        Set<String> resultSet = getFilesWithoutCrc(new File(hdfsDir, "user/celos/app/workflow"));
        Assert.assertEquals(Sets.newHashSet("file1", "file2"), resultSet);

    }

    private Set<String> getFilesWithoutCrc(File targetDir) {
        // LocalFileSystem is extends the CRCFileSysstem. so , we will get crc files at local.
        Set<String> resultSet = Sets.newHashSet();
        for (String str : targetDir.list()) {
            if (!str.endsWith(".crc")) {
                resultSet.add(str);
            }
        }
        return resultSet;
    }

}
