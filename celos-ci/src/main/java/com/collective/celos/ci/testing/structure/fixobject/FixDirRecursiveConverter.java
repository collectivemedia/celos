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
package com.collective.celos.ci.testing.structure.fixobject;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirRecursiveConverter extends AbstractFixObjectConverter<FixDir, FixDir> {

    private final AbstractFixObjectConverter<FixFile, FixFile> fixFileConverter;

    public FixDirRecursiveConverter(AbstractFixObjectConverter fixFileConverter) {
        this.fixFileConverter = fixFileConverter;
    }

    private FixFsObject transform(TestRun tr, FixFsObject object, AbstractFixObjectConverter<FixFile, FixFile> converter) throws Exception {
        if (!object.isFile()) {
            FixDir fd = (FixDir) object;
            Map<String, FixFsObject> result = Maps.newHashMap();
            Map<String, FixFsObject> map = fd.getChildren();
            for(Map.Entry<String, FixFsObject> entry : map.entrySet()) {
                result.put(entry.getKey(), transform(tr, entry.getValue(), converter));
            }
            return new FixDir(result);
        } else {
            FixFile ff = (FixFile) object;
            return converter.convert(tr, ff);
        }
    }

    @Override
    public FixDir convert(TestRun testRun, FixDir ff) throws Exception {
        return transform(testRun, ff, fixFileConverter).asDir();
    }

    public AbstractFixObjectConverter<FixFile, FixFile> getFixFileConverter() {
        return fixFileConverter;
    }
}
