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

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirFromResourceCreator implements FixObjectCreator<FixDir> {

    private final String relativePath;

    public FixDirFromResourceCreator(String path) {
        this.relativePath = path;
    }

    public FixDir create(TestRun testRun) throws Exception {
        File path = getPath(testRun);
        if (!path.isDirectory()) {
            throw new IllegalStateException("Cannot find directory: " + path);
        }
        return read(path).asDir();
    }

    public File getPath(TestRun testRun) {
        return new File(testRun.getTestCasesDir(), relativePath);
    }

    @Override
    public String getDescription(TestRun testRun) {
        return getPath(testRun).getAbsolutePath();
    }

    private FixFsObject read(File file) throws Exception {
        if (file.isDirectory()) {
            Map<String, FixFsObject> content = Maps.newHashMap();
            for (File f : file.listFiles()) {
                content.put(f.getName(), read(f));
            }
            return new FixDir(content);
        } else {
            return new FixFile(new FileInputStream(file));
        }
    }

}
