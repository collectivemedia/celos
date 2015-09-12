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
package com.collective.celos.ci.deploy;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.google.common.collect.Sets;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Set;
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
        doReturn("/some/hdfs/root").when(context).getHdfsRoot();

        File targetDir = new File(remoteHdfsFolder, "some/hdfs/root/workflow");

        deployer.deploy();

        Set<String> resultSet = getFilesWithoutCrc(targetDir);
        Assert.assertEquals(Sets.newHashSet("file2", "file1"), resultSet);
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
        doReturn("/some/hdfs/root").when(context).getHdfsRoot();

        File targetDir = new File(remoteHdfsFolder, "some/hdfs/root/workflow");
        targetDir.mkdirs();
        new File(targetDir, "file3").createNewFile();
        new File(targetDir, "file4").createNewFile();

        Assert.assertEquals(Sets.newHashSet("file3", "file4"), getFilesWithoutCrc(targetDir));

        deployer.deploy();

        Assert.assertEquals(Sets.newHashSet("file2", "file1"), getFilesWithoutCrc(targetDir));
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

    @Test
    public void testGetDestinationHdfsPath() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);

        File remoteHdfsFolder = tempDir.newFolder();
        doReturn(remoteHdfsFolder.getAbsolutePath()).when(context).getHdfsPrefix();
        doReturn("workflow").when(context).getWorkflowName();
        doReturn("/some/hdfs/root").when(context).getHdfsRoot();

        HdfsDeployer deployer = new HdfsDeployer(context);
        Path path = deployer.getDestinationHdfsPath();
        Assert.assertEquals(path, new Path(new File(remoteHdfsFolder, "some/hdfs/root/workflow").getAbsolutePath()));
    }

    @Test
    public void testUndeploy() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);

        File remoteHdfsFolder = tempDir.newFolder();

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();
        doReturn(remoteHdfsFolder.getAbsolutePath()).when(context).getHdfsPrefix();
        doReturn("workflow").when(context).getWorkflowName();
        doReturn("/some/hdfs/root").when(context).getHdfsRoot();
        
        File remoteDir = new File(remoteHdfsFolder, "some/hdfs/root/workflow");
        remoteDir.mkdirs();
        new File(remoteDir, "file").createNewFile();

        HdfsDeployer deployer = new HdfsDeployer(context);
        deployer.undeploy();

        Assert.assertFalse(remoteDir.exists());
    }


}