package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.RecursiveDirComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 10/9/14.
 */
public class FixDirFromHdfsCreatorTest {

    @Test
    public void testHdfsTreeFixObjectCreator() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);
        String path = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/create").toString();

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();
        FixDirFromHdfsCreator creator = new FixDirFromHdfsCreator(path);

        Map<String, FixObject> contentir2 = Maps.newHashMap();
        contentir2.put("file2", createFile());
        contentir2.put("file3", createFile());
        FixDir dir2 = new FixDir(contentir2);

        Map<String, FixObject> contentir1 = Maps.newHashMap();
        contentir1.put("dir2", dir2);
        contentir1.put("file1", createFile());
        FixDir dir1 = new FixDir(contentir1);

        Map<String, FixObject> contentRead = Maps.newHashMap();
        contentRead.put("dir1", dir1);
        FixDir readDir = new FixDir(contentRead);

        FixObjectCompareResult compareResult = new RecursiveDirComparer(wrapInCreator(readDir), creator).check(context);

        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    private FixFile createFile() {
        return new FixFile(IOUtils.toInputStream("1"));
    }

    private FixObjectCreator wrapInCreator(final FixDir dir) {
        return new FixObjectCreator<FixDir>() {
            @Override
            public FixDir create(CelosCiContext cc) throws Exception {
                return dir;
            }
        };
    }

}
