package com.collective.celos.ci.testing.tree;

import com.collective.celos.ci.testing.fixtures.compare.PlainFileComparer;
import com.collective.celos.ci.testing.fixtures.compare.RecursiveDirComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.collective.celos.ci.testing.structure.tree.AbstractTreeObjectProcessor;
import com.collective.celos.ci.testing.structure.tree.TreeStructureProcessor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by akonopko on 10/9/14.
 */
public class TreeStructureProcessorTest {

    @Test
    public void testTreeStructureProcessor() throws IOException {
        TreeStructureProcessor processor = new TreeStructureProcessor();
        FixDir dir1 = createDirWithSubdirsAndFile();
        InputStream inputStream2 = IOUtils.toInputStream("stream3");
        FixFile file = new FixFile(inputStream2, new PlainFileComparer());

        Map<String, FixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("file", file);
        FixDir dir = new FixDir(content, new RecursiveDirComparer());

        AbstractTreeObjectProcessorImpl holder = new AbstractTreeObjectProcessorImpl();
        processor.process(dir, holder);

        List<Path> expectedStructure = Lists.newArrayList(
                Paths.get(""),
                Paths.get("file"),
                Paths.get("dir1"),
                Paths.get("dir1/file"),
                Paths.get("dir1/dir1"),
                Paths.get("dir1/dir1/file1"),
                Paths.get("dir1/dir1/file2"),
                Paths.get("dir1/dir2"),
                Paths.get("dir1/dir2/file1"),
                Paths.get("dir1/dir2/file2"));

        Collections.sort(expectedStructure);
        Collections.sort(holder.content);
        Assert.assertEquals(holder.content, expectedStructure);
    }

    private static class AbstractTreeObjectProcessorImpl extends AbstractTreeObjectProcessor<FixObject> {

        List<Path> content = Lists.newArrayList();

        @Override
        public void process(Path path, FixObject ff) throws IOException {
            content.add(path);
        }
    }

    private FixDir createDirWithSubdirsAndFile() {
        FixDir dir1 = getFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles1();
        InputStream inputStream2 = IOUtils.toInputStream("stream");
        FixFile file = new FixFile(inputStream2, new PlainFileComparer());

        Map<String, FixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        content.put("file", file);
        return new FixDir(content, new RecursiveDirComparer());
    }

    private FixDir getFixDirWithTwoFiles1() {
        InputStream inputStream1 = IOUtils.toInputStream("stream");
        FixFile file1 = new FixFile(inputStream1, new PlainFileComparer());

        InputStream inputStream2 = IOUtils.toInputStream("stream2");
        FixFile file2 = new FixFile(inputStream2, new PlainFileComparer());

        Map<String, FixObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        return new FixDir(content1, new RecursiveDirComparer());
    }



}

