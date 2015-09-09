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
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixFileFromResourceCreator implements FixObjectCreator<FixFile> {

    private final Path relativePath;

    public FixFileFromResourceCreator(Path path) {
        this.relativePath = Util.requireNonNull(path);
    }
    
    public FixFileFromResourceCreator(String path) {
        this(Paths.get(path));
    }

    public FixFile create(TestRun testRun) throws Exception {
        File path = getPath(testRun);
        if (!path.isFile()) {
            throw new IllegalStateException("Cannot find file: " + path);
        }
        return new FixFile(new FileInputStream(path));
    }

    public File getPath(TestRun testRun) {
        return new File(testRun.getTestCasesDir(), relativePath.toString());
    }

    @Override
    public String getDescription(TestRun testRun) {
        return getPath(testRun).getAbsolutePath();
    }

}
