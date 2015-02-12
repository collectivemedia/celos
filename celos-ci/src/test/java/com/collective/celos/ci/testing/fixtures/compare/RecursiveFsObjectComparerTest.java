package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.Utils;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;
import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by akonopko on 10/9/14.
 */
public class RecursiveFsObjectComparerTest {

    @Test
    public void testSubDirs() throws Exception {
        FixDir dir1 = createParentDir1();
        FixDir dir2 = createParentDir1();

        FixObjectCompareResult compareResult = new RecursiveFsObjectComparer(Utils.wrap(dir2), Utils.wrap(dir1)).check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }


    @Test
    public void testSubDirsFail() throws Exception {
        FixDir dir1 = createParentDir1();
        FixDir dir2 = createParentDir2();

        FixObjectCompareResult compareResult = new RecursiveFsObjectComparer(Utils.wrap(dir2), Utils.wrap(dir1)).check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        String message = "dir2/file2 : File contents differed\n";
        Assert.assertEquals(compareResult.generateDescription(), message);

    }

    @Test
    public void testSubDirsFailAmnt() throws Exception {
        FixDir dir1 = createParentDir1();
        FixDir dir2 = createParentDir3();

        FixObjectCompareResult compareResult = new RecursiveFsObjectComparer(Utils.wrap(dir2), Utils.wrap(dir1)).check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        String message = "dir2 : Files found only in expected set: file3\n" +
                "dir2/file2 : File contents differed\n";
        Assert.assertEquals(compareResult.generateDescription(), message);

    }

    @Test
    public void testComparesOk() throws Exception {
        FixDir dir1 = getFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles1();

        FixObjectCompareResult compareResult = new RecursiveFsObjectComparer(Utils.wrap(dir2), Utils.wrap(dir1)).check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }


    @Test
    public void testComparesFilesOK() throws Exception {
        FixFile file1 = new FixFile(IOUtils.toInputStream("content"));
        FixFile file2 = new FixFile(IOUtils.toInputStream("content"));

        FixObjectCompareResult compareResult = new RecursiveFsObjectComparer(Utils.wrap(file1), Utils.wrap(file2)).check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    @Test
    public void testComparesFilesFail() throws Exception {
        FixFile file1 = new FixFile(IOUtils.toInputStream("content"));
        FixFile file2 = new FixFile(IOUtils.toInputStream("contend"));

        FixObjectCompareResult compareResult = new RecursiveFsObjectComparer(Utils.wrap(file1), Utils.wrap(file2)).check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
    }

/*  FIXME: fails
    @Test
    public void testComparesWrongTypes() throws Exception {
        FixFile file1 = new FixFile(IOUtils.toInputStream("content"));
        FixDir file2 = getFixDirWithTwoFiles1();

        FixObjectCompareResult compareResult = new RecursiveFsObjectComparer(Utils.wrap(file1), Utils.wrap(file2)).check(null);
        Assert.assertEquals("RecursiveFsObjectComparer: expected is [File] and actual is [Dir]", compareResult.generateDescription());
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
    }
*/
    @Test
    public void testComparesWrongTypesInSubdirs() throws Exception {
        FixDir dir1 = getFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFilesWrongTypes();

        FixObjectCompareResult compareResult = new RecursiveFsObjectComparer(Utils.wrap(dir2), Utils.wrap(dir1)).check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        Assert.assertEquals(compareResult.generateDescription(), "Files have different types: file2: expected is [Dir] and actual is [File]\n");
    }


    @Test
    public void testComparesFailWrongContent() throws Exception {
        FixDir dir1 = getFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles2();

        FixObjectCompareResult compareResult = new RecursiveFsObjectComparer(Utils.wrap(dir2), Utils.wrap(dir1)).check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        String message = "file2 : File contents differed\n";
        Assert.assertEquals(compareResult.generateDescription(), message);
    }

    @Test
    public void testComparesFailWrongNumberOfFiles() throws Exception {
        FixDir dir1 = getFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles3();

        FixObjectCompareResult compareResult = new RecursiveFsObjectComparer(Utils.wrap(dir2), Utils.wrap(dir1)).check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        String message = "Files found only in expected set: file3\n" +
                "file2 : File contents differed\n";
        Assert.assertEquals(compareResult.generateDescription(), message);
    }

    private FixDir createParentDir1() {
        FixDir dir1 = getFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles1();
        Map<String, FixFsObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        return new FixDir(content);
    }

    private FixDir createParentDir2() {
        FixDir dir1 = getFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles2();
        Map<String, FixFsObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        return new FixDir(content);
    }

    private FixDir createParentDir3() {
        FixDir dir1 = getFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles3();
        Map<String, FixFsObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        return new FixDir(content);
    }


    private FixDir getFixDirWithTwoFiles1() {
        InputStream inputStream1 = IOUtils.toInputStream("stream");
        FixFile file1 = new FixFile(inputStream1);

        InputStream inputStream2 = IOUtils.toInputStream("stream");
        FixFile file2 = new FixFile(inputStream2);

        Map<String, FixFsObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        return new FixDir(content1);
    }

    private FixDir getFixDirWithTwoFilesWrongTypes() {
        InputStream inputStream1 = IOUtils.toInputStream("stream");
        FixFile file1 = new FixFile(inputStream1);

        FixDir file2 = new FixDir(Maps.<String, FixFsObject>newHashMap());

        Map<String, FixFsObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        return new FixDir(content1);
    }

    private FixDir getFixDirWithTwoFiles3() {
        InputStream inputStream1 = IOUtils.toInputStream("stream");
        FixFile file1 = new FixFile(inputStream1);

        InputStream inputStream2 = IOUtils.toInputStream("stream2");
        FixFile file2 = new FixFile(inputStream2);

        InputStream inputStream3 = IOUtils.toInputStream("stream");
        FixFile file3 = new FixFile(inputStream3);

        Map<String, FixFsObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        content1.put("file3", file3);
        return new FixDir(content1);
    }

    private FixDir getFixDirWithTwoFiles2() {
        InputStream inputStream1 = IOUtils.toInputStream("stream");
        FixFile file1 = new FixFile(inputStream1);

        InputStream inputStream2 = IOUtils.toInputStream("stream3");
        FixFile file2 = new FixFile(inputStream2);

        Map<String, FixFsObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        return new FixDir(content1);
    }


}
