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
package com.collective.celos.ci.testing.tree;

import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;
import com.collective.celos.ci.testing.structure.tree.TreeObjectProcessor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by akonopko on 10/9/14.
 */
public class TreeStructureProcessorTest {

    @Test
    public void testTreeStructureProcessor() throws IOException {
        FixDir dir1 = createDirWithSubdirsAndFile();
        InputStream inputStream2 = IOUtils.toInputStream("stream3");
        FixFile file = new FixFile(inputStream2);

        Map<String, FixFsObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("file", file);
        FixDir dir = new FixDir(content);

        TreeObjectProcessorImpl holder = new TreeObjectProcessorImpl();
        TreeObjectProcessor.process(dir, holder);

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

    private static class TreeObjectProcessorImpl extends TreeObjectProcessor<FixFsObject> {

        List<Path> content = Lists.newArrayList();

        @Override
        public void process(Path path, FixFsObject ff) throws IOException {
            content.add(path);
        }
    }

    private FixDir createDirWithSubdirsAndFile() {
        FixDir dir1 = getFixDirWithTwoFiles1();
        FixDir dir2 = getFixDirWithTwoFiles1();
        InputStream inputStream2 = IOUtils.toInputStream("stream");
        FixFile file = new FixFile(inputStream2);

        Map<String, FixFsObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        content.put("file", file);
        return new FixDir(content);
    }

    private FixDir getFixDirWithTwoFiles1() {
        InputStream inputStream1 = IOUtils.toInputStream("stream");
        FixFile file1 = new FixFile(inputStream1);

        InputStream inputStream2 = IOUtils.toInputStream("stream2");
        FixFile file2 = new FixFile(inputStream2);

        Map<String, FixFsObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        return new FixDir(content1);
    }



}

