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
package com.collective.celos.ci.testing.fixtures.deploy;

import com.collective.celos.Util;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;
import com.collective.celos.ci.testing.structure.tree.TreeObjectProcessor;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.Map;

/**
 * Created by akonopko on 21.11.14.
 */
public class HdfsInputDeployer implements FixtureDeployer {

    private final FixObjectCreator<FixFsObject> fixObjectCreator;
    private final Path path;

    public HdfsInputDeployer(FixObjectCreator<FixFsObject> fixObjectCreator, String path) {
        this.fixObjectCreator = fixObjectCreator;
        this.path = new Path(path);
    }

    @Override
    public void undeploy(TestRun testRun) throws Exception {
        // do nothing, because all HDFS cleanup is already done in TestRun explicitly
    }

    @Override
    public void deploy(TestRun testRun) throws Exception {
        FileSystem fileSystem = testRun.getCiContext().getFileSystem();

        CollectFilesAndPathsProcessor pathToFile = new CollectFilesAndPathsProcessor();
        TreeObjectProcessor.process(fixObjectCreator.create(testRun), pathToFile);

        Path pathPrefixed = new Path(Util.augmentHdfsPath(testRun.getHdfsPrefix(), path.toString()));
        for (java.nio.file.Path childPath: pathToFile.pathToFiles.keySet()) {
            Path pathTo = new Path(pathPrefixed, childPath.toString());
            fileSystem.mkdirs(pathTo.getParent());

            FSDataOutputStream outputStream = fileSystem.create(pathTo);
            try {
                IOUtils.copy(pathToFile.pathToFiles.get(childPath).getContent(), outputStream);
            } finally {
                outputStream.flush();
                outputStream.close();
            }

        }
    }

    @Override
    public void validate(TestRun testRun) throws Exception {
        Path pathToCheck = new Path(testRun.getHdfsPrefix(), path);
        FileSystem fileSystem = testRun.getCiContext().getFileSystem();
        if (fileSystem.exists(pathToCheck)) {
            throw new CelosCiDirtyStateException("Celos-CI temporary path still exists: " + pathToCheck);
        }
    }

    public FixObjectCreator<FixFsObject> getFixObjectCreator() {
        return fixObjectCreator;
    }

    public Path getPath() {
        return path;
    }

    private static class CollectFilesAndPathsProcessor extends TreeObjectProcessor<FixFsObject> {

        private final Map<java.nio.file.Path, FixFile> pathToFiles = Maps.newHashMap();

        @Override
        public void process(java.nio.file.Path path, FixFsObject fo) throws IOException {
            if (fo.isFile()) {
                pathToFiles.put(path, fo.asFile());
            }
        }
    }

}
