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
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirHierarchyCreator implements FixObjectCreator<FixDir> {

    private final Map<String, FixObjectCreator<FixFsObject>> hierarchy;

    public FixDirHierarchyCreator(Map<String, FixObjectCreator<FixFsObject>> hierarchy) {
        this.hierarchy = hierarchy;
    }

    public FixDir create(TestRun testRun) throws Exception {
        Map<String, FixFsObject> content = Maps.newHashMap();
        for (Map.Entry<String, FixObjectCreator<FixFsObject>> entry : hierarchy.entrySet()) {
            content.put(entry.getKey(), entry.getValue().create(testRun));
        }
        return new FixDir(content);
    }

    @Override
    public String getDescription(TestRun testRun) {
        return hierarchy.keySet().toString();
    }

}
