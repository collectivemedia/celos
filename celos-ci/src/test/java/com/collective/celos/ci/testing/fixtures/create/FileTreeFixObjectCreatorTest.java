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
package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.Utils;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.RecursiveFsObjectComparer;
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
        FixObjectCompareResult compareResult = new RecursiveFsObjectComparer(Utils.wrap(readDir), creator).check(testRun);

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
        FixObjectCompareResult compareResult = new RecursiveFsObjectComparer(Utils.wrap(readDir), creator).check(testRun);

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
