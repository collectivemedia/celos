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

import com.collective.celos.Util;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class OutputFixDirFromHdfsCreator implements FixObjectCreator<FixDir> {

    private final Path path;

    public OutputFixDirFromHdfsCreator(String path) {
        this(new Path(path));
    }

    public OutputFixDirFromHdfsCreator(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public FixDir create(TestRun testRun) throws Exception {
        Path fullPath = new Path(Util.augmentHdfsPath(testRun.getCiContext().getHdfsPrefix(), path.toString()));
        return read(fullPath, testRun.getCiContext()).asDir();
    }

    @Override
    public String getDescription(TestRun testRun) throws URISyntaxException {
        return Util.augmentHdfsPath(testRun.getCiContext().getHdfsPrefix(), path.toString());
    }

    private FixFsObject read(Path path, CelosCiContext context) throws Exception {
        FileStatus fileStatus = context.getFileSystem().getFileStatus(path);
        if (fileStatus.isDirectory()) {
            Map<String, FixFsObject> content = Maps.newHashMap();
            FileStatus[] statuses = context.getFileSystem().listStatus(fileStatus.getPath());
            for (int i=0; i < statuses.length; i++) {
                FileStatus childStatus = statuses[i];
                FixFsObject fixObject = read(childStatus.getPath(), context);
                content.put(childStatus.getPath().getName(), fixObject);
            }
            return new FixDir(content);
        } else {
            return new FixFile(context.getFileSystem().open(fileStatus.getPath()));
        }
    }

}
