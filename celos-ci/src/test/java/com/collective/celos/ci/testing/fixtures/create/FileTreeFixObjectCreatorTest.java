package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.Utils;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.RecursiveDirComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;
import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 10/9/14.
 */
public class FileTreeFixObjectCreatorTest {

    private TestRun testRun;

    @Before
    public void setUp() {
        testRun = mock(TestRun.class);
        doReturn(new File("/")).when(testRun).getTestCasesDir();
    }

    @Test
    public void testFixDirCreator() throws Exception {
        String path = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/create").toURI()).getAbsolutePath();

        Map<String, FixFsObject> contentir2 = Maps.newHashMap();
        contentir2.put("file2", createFile());
        contentir2.put("file3", createFile());
        FixDir dir2 = new FixDir(contentir2);

        Map<String, FixFsObject> contentir1 = Maps.newHashMap();
        contentir1.put("dir2", dir2);
        contentir1.put("file1", createFile());
        FixDir dir1 = new FixDir(contentir1);


        Map<String, FixFsObject> contentRead = Maps.newHashMap();
        contentRead.put("dir1", dir1);
        FixDir readDir = new FixDir(contentRead);

        FixDirFromResourceCreator creator = new FixDirFromResourceCreator(path);
        FixObjectCompareResult compareResult = new RecursiveDirComparer(Utils.wrap(readDir), creator).check(testRun);

        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    @Test (expected = IllegalStateException.class)
    public void testFixDirCreatorFails() throws Exception {
        String path = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/create/dir1/file1").toURI()).getAbsolutePath();

        Map<String, FixFsObject> contentir2 = Maps.newHashMap();
        contentir2.put("file2", createFile());
        contentir2.put("file3", createFile());
        FixDir dir2 = new FixDir(contentir2);

        Map<String, FixFsObject> contentir1 = Maps.newHashMap();
        contentir1.put("dir2", dir2);
        contentir1.put("file1", createFile());
        FixDir dir1 = new FixDir(contentir1);


        Map<String, FixFsObject> contentRead = Maps.newHashMap();
        contentRead.put("dir1", dir1);
        FixDir readDir = new FixDir(contentRead);

        FixDirFromResourceCreator creator = new FixDirFromResourceCreator(path);
        FixObjectCompareResult compareResult = new RecursiveDirComparer(Utils.wrap(readDir), creator).check(testRun);

        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    @Test(expected = IllegalStateException.class)
    public void testFixFileCreatorFails() throws Exception {
        String path = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/create").toURI()).getAbsolutePath();
        FixFileFromResourceCreator creator = new FixFileFromResourceCreator(path);
        creator.create(testRun);
    }

    @Test
    public void testFixFileCreator() throws Exception {
        String path = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/create/dir1/file1").toURI()).getAbsolutePath();
        FixFileFromResourceCreator creator = new FixFileFromResourceCreator(path);
        creator.create(testRun);
    }

    private FixFile createFile() {
        return new FixFile(IOUtils.toInputStream("1"));
    }

}
