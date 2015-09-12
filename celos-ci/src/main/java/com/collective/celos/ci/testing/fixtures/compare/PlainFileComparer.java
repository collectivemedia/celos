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
package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

/**
 * Created by akonopko on 10/7/14.
 */
public class PlainFileComparer implements FixtureComparer {

    private final InputStream content;
    private final FixFile file;

    public PlainFileComparer(InputStream content, FixFile file) {
        this.content = content;
        this.file = file;
    }

    public FixObjectCompareResult check(TestRun testRun) throws Exception {
        if (!IOUtils.contentEquals(content, file.getContent())) {
            return FixObjectCompareResult.failed("File contents differed");
        }
        return FixObjectCompareResult.SUCCESS;
    }

}
