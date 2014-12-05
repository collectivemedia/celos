package com.collective.celos.ci.testing.fixtures.deploy;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 10/10/14.
 */
public class HdfsFixtureDeployerTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testHdfsFixtureDeployer() throws Exception {

        File tempFolder = tempDir.newFolder();

        CelosCiContext context = mock(CelosCiContext.class);
        HdfsFixtureDeployer deployer = new HdfsFixtureDeployer(context);

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();
        doReturn(tempFolder.getAbsolutePath()).when(context).getHdfsPrefix();

        deployer.deploy(createFixDirToDeploy());

        File dir1 = new File(tempFolder, "dir1");
        File dir2 = new File(tempFolder, "dir2");

        assertDirContents(tempFolder, Sets.newHashSet("dir1", "dir2"));
        assertDirContents(dir1, Sets.newHashSet("file1", "file2"));
        assertDirContents(dir2, Sets.newHashSet("file1", "file2"));
        Assert.assertEquals(IOUtils.toString(new FileInputStream(new File(dir1, "file1"))), "stream");
        Assert.assertEquals(IOUtils.toString(new FileInputStream(new File(dir1, "file2"))), "stream");
        Assert.assertEquals(IOUtils.toString(new FileInputStream(new File(dir2, "file1"))), "stream");
        Assert.assertEquals(IOUtils.toString(new FileInputStream(new File(dir2, "file2"))), "stream");
    }

    private void assertDirContents(File targetDir, Set<String> dirContents) {
        // LocalFileSystem is extends the CRCFileSysstem. so , we will get crc files at local.
        Set<String> resultSet = Sets.newHashSet();
        for (String str : targetDir.list()) {
            if (!str.endsWith(".crc")) {
                resultSet.add(str);
            }
        }
        Assert.assertEquals(resultSet, dirContents);
    }

    private FixDir getFixDirWithTwoFiles1() {
        InputStream inputStream1 = IOUtils.toInputStream("stream");
        FixFile file1 = new FixFile(inputStream1);

        InputStream inputStream2 = IOUtils.toInputStream("stream");
        FixFile file2 = new FixFile(inputStream2);

        Map<String, FixObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        return new FixDir(content1);
    }

    private FixDir createFixDirToDeploy() {
        FixDir dir1 = getFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles1();
        Map<String, FixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        return new FixDir(content);
    }

}
