package com.collective.celos.ci.testing.compare;

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
import org.junit.Test;

import java.io.InputStream;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 10/9/14.
 */
public class RecursiveDirComparerTest {


    @Test
    public void testSubDirs() throws Exception {
        OutFixDir dir1 = createOutParentDir1();
        FixDir dir2 = createParentDir1();

        FixObjectCompareResult compareResult = new RecursiveDirComparer().compare(dir1, dir2);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }


    @Test
    public void testSubDirsFail() throws Exception {
        OutFixDir dir1 = createOutParentDir1();
        FixDir dir2 = createParentDir2();

        FixObjectCompareResult compareResult = new RecursiveDirComparer().compare(dir1, dir2);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        String message = "dir2/file2 : File contents differed\n";
        Assert.assertEquals(compareResult.generateDescription(), message);

    }

    @Test
    public void testSubDirsFailAmnt() throws Exception {
        OutFixDir dir1 = createOutParentDir1();
        FixDir dir2 = createParentDir3();

        FixObjectCompareResult compareResult = new RecursiveDirComparer().compare(dir1, dir2);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        String message = "dir2 : Files found only in result set: file3\n" +
                "dir2/file2 : File contents differed\n";
        Assert.assertEquals(compareResult.generateDescription(), message);

    }

    @Test
    public void testComparesOk() throws Exception {
        OutFixDir dir1 = getOutFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles1();

        RecursiveDirComparer comparer = new RecursiveDirComparer();

        FixObjectCompareResult compareResult = comparer.compare(dir1, dir2);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    @Test
    public void testComparesFailWrongContent() throws Exception {
        OutFixDir dir1 = getOutFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles2();

        RecursiveDirComparer comparer = new RecursiveDirComparer();

        FixObjectCompareResult compareResult = comparer.compare(dir1, dir2);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        String message = "file2 : File contents differed\n";
        Assert.assertEquals(compareResult.generateDescription(), message);
    }

    @Test
    public void testComparesFailWrongNumberOfFiles() throws Exception {
        OutFixDir dir1 = getOutFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles3();

        RecursiveDirComparer comparer = new RecursiveDirComparer();

        FixObjectCompareResult compareResult = comparer.compare(dir1, dir2);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        String message = "Files found only in result set: file3\n" +
                "file2 : File contents differed\n";
        Assert.assertEquals(compareResult.generateDescription(), message);
    }


    private OutFixDir createOutParentDir1() {
        OutFixDir dir1 = getOutFixDirWithTwoFiles1();
        OutFixDir dir2 = getOutFixDirWithTwoFiles1();
        Map<String, OutFixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        return new OutFixDir(content, new RecursiveDirComparer());
    }

    private OutFixDir createOutParentDir2() {
        OutFixDir dir1 = getOutFixDirWithTwoFiles1();
        OutFixDir dir2 = getOutFixDirWithTwoFiles2();
        Map<String, OutFixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        return new OutFixDir(content, new RecursiveDirComparer());
    }

    private OutFixDir createOutParentDir3() {
        OutFixDir dir1 = getOutFixDirWithTwoFiles1();
        OutFixDir dir2 = getOutFixDirWithTwoFiles3();
        Map<String, OutFixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        return new OutFixDir(content, new RecursiveDirComparer());
    }


    private OutFixDir getOutFixDirWithTwoFiles1() {
        InputStream inputStream1 = IOUtils.toInputStream("stream");
        OutFixFile file1 = new OutFixFile(inputStream1, new PlainFileComparer());

        InputStream inputStream2 = IOUtils.toInputStream("stream");
        OutFixFile file2 = new OutFixFile(inputStream2, new PlainFileComparer());

        Map<String, OutFixObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        return new OutFixDir(content1, new RecursiveDirComparer());
    }

    private OutFixDir getOutFixDirWithTwoFiles3() {
        InputStream inputStream1 = IOUtils.toInputStream("stream");
        OutFixFile file1 = new OutFixFile(inputStream1, new PlainFileComparer());

        InputStream inputStream2 = IOUtils.toInputStream("stream2");
        OutFixFile file2 = new OutFixFile(inputStream2, new PlainFileComparer());

        InputStream inputStream3 = IOUtils.toInputStream("stream");
        OutFixFile file3 = new OutFixFile(inputStream3, new PlainFileComparer());

        Map<String, OutFixObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        content1.put("file3", file3);
        return new OutFixDir(content1, new RecursiveDirComparer());
    }

    private OutFixDir getOutFixDirWithTwoFiles2() {
        InputStream inputStream1 = IOUtils.toInputStream("stream");
        OutFixFile file1 = new OutFixFile(inputStream1, new PlainFileComparer());

        InputStream inputStream2 = IOUtils.toInputStream("stream3");
        OutFixFile file2 = new OutFixFile(inputStream2, new PlainFileComparer());

        Map<String, OutFixObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        return new OutFixDir(content1, new RecursiveDirComparer());
    }

    private FixDir createParentDir1() {
        FixDir dir1 = getFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles1();
        Map<String, FixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        return new FixDir(content);
    }

    private FixDir createParentDir2() {
        FixDir dir1 = getFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles2();
        Map<String, FixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        return new FixDir(content);
    }

    private FixDir createParentDir3() {
        FixDir dir1 = getFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles3();
        Map<String, FixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        return new FixDir(content);
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

    private FixDir getFixDirWithTwoFiles3() {
        InputStream inputStream1 = IOUtils.toInputStream("stream");
        FixFile file1 = new FixFile(inputStream1);

        InputStream inputStream2 = IOUtils.toInputStream("stream2");
        FixFile file2 = new FixFile(inputStream2);

        InputStream inputStream3 = IOUtils.toInputStream("stream");
        FixFile file3 = new FixFile(inputStream3);

        Map<String, FixObject> content1 = Maps.newHashMap();
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

        Map<String, FixObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        return new FixDir(content1);
    }


}
