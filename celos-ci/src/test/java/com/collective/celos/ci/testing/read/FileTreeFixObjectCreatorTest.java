package com.collective.celos.ci.testing.read;

import com.collective.celos.ci.testing.fixtures.read.FileTreeFixObjectCreator;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.PlainFileComparer;
import com.collective.celos.ci.testing.fixtures.compare.RecursiveDirComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 10/9/14.
 */
public class FileTreeFixObjectCreatorTest {

    @Test
    public void testFileTreeFixObjectCreator() throws Exception {
        String path = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/read").toURI()).getAbsolutePath();

        FileTreeFixObjectCreator creator = new FileTreeFixObjectCreator(path);

        Map<String, FixObject> contentir2 = Maps.newHashMap();
        contentir2.put("file2", createFile());
        contentir2.put("file3", createFile());
        FixDir dir2 = new FixDir(contentir2, new RecursiveDirComparer());

        Map<String, FixObject> contentir1 = Maps.newHashMap();
        contentir1.put("dir2", dir2);
        contentir1.put("file1", createFile());
        FixDir dir1 = new FixDir(contentir1, new RecursiveDirComparer());


        Map<String, FixObject> contentRead = Maps.newHashMap();
        contentRead.put("dir1", dir1);
        FixDir readDir = new FixDir(contentRead, new RecursiveDirComparer());

        FixObject fixObject = creator.create();
        FixObjectCompareResult compareResult = fixObject.compare(readDir);

        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    private FixFile createFile() {
        return new FixFile(IOUtils.toInputStream("1"), new PlainFileComparer());
    }
}
