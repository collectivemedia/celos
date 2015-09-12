/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos.ci.testing.fixtures.convert;

import com.collective.celos.ci.Utils;
import com.collective.celos.ci.mode.test.TestRun;
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
public class FixDirRecursiveConverterTest {

    public static final String CHECKSTR = "lowercase";

    @Test
    public void testFixDirTreeConverter() throws Exception {
        FixDir dir = createDirWithFileContent("lowercase");
        FixDirRecursiveConverter converter = new FixDirRecursiveConverter(new UpperCaseStringFixFileConverter());

        FixDir transformed = converter.convert(null, dir);

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

    private static class TreeObjectProcessorImpl extends TreeObjectProcessor<FixFsObject> {

        TreeSet<Path> visited = new TreeSet<>();

        @Override
        public void process(Path path, FixFsObject ff) throws IOException {
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

        Map<String, FixFsObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("file", file);
        return new FixDir(content);
    }

    private FixDir createOutDirWithSubdirsAndFile(String fileContent) {
        FixDir dir1 = getFixDirWithTwoFiles1(fileContent);
        FixDir dir2 = getFixDirWithTwoFiles1(fileContent);
        InputStream inputStream2 = IOUtils.toInputStream(fileContent);
        FixFile file = new FixFile(inputStream2);

        Map<String, FixFsObject> content = Maps.newHashMap();
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

        Map<String, FixFsObject> content = Maps.newHashMap();
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

        Map<String, FixFsObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        return new FixDir(content1);
    }

}
