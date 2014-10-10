package com.collective.celos.ci.testing.read;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.testing.fixtures.read.HdfsTreeFixObjectCreator;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.PlainFileComparer;
import com.collective.celos.ci.testing.fixtures.compare.RecursiveDirComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.collective.celos.ci.testing.structure.outfixture.OutFixDir;
import com.collective.celos.ci.testing.structure.outfixture.OutFixFile;
import com.collective.celos.ci.testing.structure.outfixture.OutFixObject;
import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.junit.Test;

import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 10/9/14.
 */
public class HdfsTreeFixObjectCreatorTest {

    @Test
    public void testHdfsTreeFixObjectCreator() throws Exception {
        CelosCiContext context = mock(CelosCiContext.class);
        String path = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/create").toString();

        doReturn(LocalFileSystem.get(new Configuration())).when(context).getFileSystem();
        HdfsTreeFixObjectCreator creator = new HdfsTreeFixObjectCreator(context, path);

        Map<String, OutFixObject> contentir2 = Maps.newHashMap();
        contentir2.put("file2", createFile());
        contentir2.put("file3", createFile());
        OutFixDir dir2 = new OutFixDir(contentir2, new RecursiveDirComparer());

        Map<String, OutFixObject> contentir1 = Maps.newHashMap();
        contentir1.put("dir2", dir2);
        contentir1.put("file1", createFile());
        OutFixDir dir1 = new OutFixDir(contentir1, new RecursiveDirComparer());


        Map<String, OutFixObject> contentRead = Maps.newHashMap();
        contentRead.put("dir1", dir1);
        OutFixDir readDir = new OutFixDir(contentRead, new RecursiveDirComparer());

        FixObject fixObject = creator.createInFixture();
        FixObjectCompareResult compareResult = readDir.compare(fixObject);

        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    private OutFixFile createFile() {
        return new OutFixFile(IOUtils.toInputStream("1"), new PlainFileComparer());
    }
}
