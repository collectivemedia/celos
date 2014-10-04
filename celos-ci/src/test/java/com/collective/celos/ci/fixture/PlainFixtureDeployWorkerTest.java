package com.collective.celos.ci.fixture;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.plain.PlainFixtureDeployWorker;
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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 9/18/14.
 */
public class PlainFixtureDeployWorkerTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void processPair() throws Exception {
        PlainFixtureDeployWorker worker = new PlainFixtureDeployWorker();
        CelosCiContext context = mock(CelosCiContext.class);

        File hdfsFolder = tempDir.newFolder();

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();

        File localFile = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/compare/equal1").toURI());
        Path hdfsFile = new Path(new File(hdfsFolder, "equal1").toURI());

        Assert.assertArrayEquals(hdfsFolder.list(), new String[]{});
        worker.processPair(context, localFile, hdfsFile);
        Assert.assertEquals(getFilesWithoutCrc(hdfsFolder), Sets.newHashSet("equal1"));
    }

    @Test(expected = IllegalStateException.class)
    public void processPairNoLocalFile() throws Exception {
        PlainFixtureDeployWorker worker = new PlainFixtureDeployWorker();
        CelosCiContext context = mock(CelosCiContext.class);

        File hdfsFolder = tempDir.newFolder();

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();

        File localFile = new File("blah");
        Path hdfsFile = new Path(new File(hdfsFolder, "equal1").toURI());

        Assert.assertArrayEquals(hdfsFolder.list(), new String[]{});
        worker.processPair(context, localFile, hdfsFile);
    }

    @Test(expected = IllegalStateException.class)
    public void processPairExistsOnHdfs() throws Exception {
        PlainFixtureDeployWorker worker = new PlainFixtureDeployWorker();
        CelosCiContext context = mock(CelosCiContext.class);

        File hdfsFolder = tempDir.newFolder();

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();

        File localFile = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/compare/equal1").toURI());
        Path hdfsFile = new Path(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/compare/equal2").toURI());

        Assert.assertArrayEquals(hdfsFolder.list(), new String[]{});
        worker.processPair(context, localFile, hdfsFile);
        Assert.assertEquals(getFilesWithoutCrc(hdfsFolder), Sets.newHashSet("equal1"));
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
