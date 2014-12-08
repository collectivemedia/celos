package com.collective.celos.ci.testing.tree;

import com.collective.celos.ci.Utils;
import com.collective.celos.ci.testing.structure.fixobject.*;
import com.collective.celos.ci.testing.structure.tree.TreeObjectProcessor;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirTreeConverterTest {

    public static final String CHECKSTR = "lowercase";

    @Test
    public void testFixDirTreeConverter() throws Exception {
        FixDir dir = createDirWithFileContent("lowercase");
        FixDirTreeConverter converter = new FixDirTreeConverter(Utils.wrap(dir), new UpperCaseStringFixFileConverter());

        FixDir transformed = converter.create(null);

        TreeObjectProcessorImpl holder = new TreeObjectProcessorImpl();
        TreeObjectProcessor.process(transformed, holder);
        Set<Path> visited = Sets.newHashSet(Paths.get("dir1/dir1/file1"),
                                            Paths.get("dir1/dir1/file2"),
                                            Paths.get("dir1/dir2/file1"),
                                            Paths.get("dir1/dir2/file2"),
                                            Paths.get("dir1/file"),
                                            Paths.get("file"));
        Assert.assertEquals(holder.visited, visited);
    }

    private static class TreeObjectProcessorImpl extends TreeObjectProcessor<FixObject> {

        TreeSet<Path> visited = new TreeSet<>();

        @Override
        public void process(Path path, FixObject ff) throws IOException {
            if (ff.isFile()) {
                String content = IOUtils.toString(ff.asFile().getContent()).toUpperCase();
                Assert.assertEquals(content, CHECKSTR.toUpperCase());
                visited.add(path);
            }
        }
    }


    private FixDir createDirWithFileContent(String fileContent) {
        FixDir dir1 = createDirWithSubdirsAndFile(fileContent);
        InputStream inputStream2 = IOUtils.toInputStream(fileContent);
        FixFile file = new FixFile(inputStream2);

        Map<String, FixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("file", file);
        return new FixDir(content);
    }

    private static class UpperCaseStringFixFileConverter extends AbstractFixFileConverter {

        @Override
        public FixFile convert(FixFile ff) throws IOException {
            String newContent = IOUtils.toString(ff.getContent()).toUpperCase();
            return new FixFile(IOUtils.toInputStream(newContent));
        }
    }

    private FixDir createOutDirWithSubdirsAndFile(String fileContent) {
        FixDir dir1 = getFixDirWithTwoFiles1(fileContent);
        FixDir dir2 = getFixDirWithTwoFiles1(fileContent);
        InputStream inputStream2 = IOUtils.toInputStream(fileContent);
        FixFile file = new FixFile(inputStream2);

        Map<String, FixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        content.put("file", file);
        return new FixDir(content);
    }


    private FixDir createDirWithSubdirsAndFile(String fileContent) {
        FixDir dir1 = getFixDirWithTwoFiles1(fileContent);
        FixDir dir2 = getFixDirWithTwoFiles1(fileContent);
        InputStream inputStream2 = IOUtils.toInputStream(fileContent);
        FixFile file = new FixFile(inputStream2);

        Map<String, FixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        content.put("file", file);
        return new FixDir(content);
    }

    private FixDir getFixDirWithTwoFiles1(String fileContent) {
        InputStream inputStream1 = IOUtils.toInputStream(fileContent);
        FixFile file1 = new FixFile(inputStream1);

        InputStream inputStream2 = IOUtils.toInputStream(fileContent);
        FixFile file2 = new FixFile(inputStream2);

        Map<String, FixObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        return new FixDir(content1);
    }

}
