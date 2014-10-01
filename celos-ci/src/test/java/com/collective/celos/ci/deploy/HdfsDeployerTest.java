package com.collective.celos.ci.deploy;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by akonopko on 10/1/14.
 */
public class HdfsDeployerTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test(expected = IllegalStateException.class)
    public void testDeployThrowsExceptionNoDir() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);
        HdfsDeployer deployer = new HdfsDeployer(context);

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();
        doReturn(new File("nodir" + UUID.randomUUID())).when(context).getDeployDir();

        deployer.deploy();
    }


    @Test
    public void testDeployRemoteDirNotExist() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);
        HdfsDeployer deployer = new HdfsDeployer(context);

        File localFolder = tempDir.newFolder();
        File remoteHdfsFolder = tempDir.newFolder();

        File localHdfsFolder = new File(localFolder, "hdfs");
        localHdfsFolder.mkdirs();
        new File(localHdfsFolder, "file1").createNewFile();
        new File(localHdfsFolder, "file2").createNewFile();

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();
        doReturn(localFolder).when(context).getDeployDir();
        doReturn(remoteHdfsFolder.getAbsolutePath()).when(context).getHdfsPrefix();
        doReturn("workflow").when(context).getWorkflowName();

        File targetDir = new File(remoteHdfsFolder, "user/celos/app/workflow");

        deployer.deploy();

        // LocalFileSystem is extends the CRCFileSysstem. so , we will get crc files at local.
        Assert.assertArrayEquals(new String[]{".file1.crc", "file2", ".file2.crc", "file1"}, targetDir.list());
    }

    @Test
    public void testDeployRemoteDirExist() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);
        HdfsDeployer deployer = new HdfsDeployer(context);

        File localFolder = tempDir.newFolder();
        File remoteHdfsFolder = tempDir.newFolder();

        File localHdfsFolder = new File(localFolder, "hdfs");
        localHdfsFolder.mkdirs();
        new File(localHdfsFolder, "file1").createNewFile();
        new File(localHdfsFolder, "file2").createNewFile();

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();
        doReturn(localFolder).when(context).getDeployDir();
        doReturn(remoteHdfsFolder.getAbsolutePath()).when(context).getHdfsPrefix();
        doReturn("workflow").when(context).getWorkflowName();

        File targetDir = new File(remoteHdfsFolder, "user/celos/app/workflow");
        targetDir.mkdirs();
        new File(targetDir, "file3").createNewFile();
        new File(targetDir, "file4").createNewFile();
        Assert.assertArrayEquals(new String[]{"file3", "file4"}, targetDir.list());

        deployer.deploy();

        // LocalFileSystem is extends the CRCFileSysstem. so , we will get crc files at local.
        Assert.assertArrayEquals(new String[]{".file1.crc", "file2", ".file2.crc", "file1"}, targetDir.list());
    }

    @Test
    public void testGetDestinationHdfsPath() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);

        File remoteHdfsFolder = tempDir.newFolder();
        doReturn(remoteHdfsFolder.getAbsolutePath()).when(context).getHdfsPrefix();
        doReturn("workflow").when(context).getWorkflowName();

        HdfsDeployer deployer = new HdfsDeployer(context);
        Path path = deployer.getDestinationHdfsPath();
        Assert.assertEquals(path, new Path(new File(remoteHdfsFolder, "user/celos/app/workflow").getAbsolutePath()));
    }

    @Test
    public void testUndeploy() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);

        File remoteHdfsFolder = tempDir.newFolder();

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();
        doReturn(remoteHdfsFolder.getAbsolutePath()).when(context).getHdfsPrefix();
        doReturn("workflow").when(context).getWorkflowName();

        File remoteDir = new File(remoteHdfsFolder, "user/celos/app/workflow");
        remoteDir.mkdirs();
        new File(remoteDir, "file").createNewFile();

        HdfsDeployer deployer = new HdfsDeployer(context);
        deployer.undeploy();

        Assert.assertFalse(remoteDir.exists());
    }


}