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

package com.collective.celos.ci.testing.fixtures.compare.json;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.compare.CompareHelper;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.FixtureComparer;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by akonopko on 10/7/14.
 */
public class JsonContentsComparer implements FixtureComparer {

    private final JsonElementConstructor constructor;
    private final Set<String> ignorePaths;
    private final FixObjectCreator<FixFile> expectedDataCreator;
    private final FixObjectCreator<? extends FixFsObject> actualDataCreator;

    public JsonContentsComparer(Set<String> ignorePaths, FixObjectCreator<FixFile> expectedDataCreator, FixObjectCreator<? extends FixFsObject> actualDataCreator) {
        this.ignorePaths = ignorePaths;
        this.expectedDataCreator = expectedDataCreator;
        this.actualDataCreator = actualDataCreator;
        this.constructor = new JsonElementConstructor();
    }

    public FixObjectCompareResult check(TestRun testRun) throws Exception {

        FixFsObject fixFsObject = actualDataCreator.create(testRun);

        Map<JsonElement, Integer> actualRes = Maps.newHashMap();
        if  (fixFsObject.isFile()) {
            fillMapWithJsonFromIS(actualRes, fixFsObject.asFile().getContent());
        } else {
            Collection<FixFsObject> children = fixFsObject.asDir().getChildren().values();
            getJsonEntityCountMap(children, actualRes);
        }

        Map<JsonElement, Integer> expectedRes = Maps.newHashMap();
        fillMapWithJsonFromIS(expectedRes, expectedDataCreator.create(testRun).getContent());

        return CompareHelper.compareEntityNumber(testRun, actualDataCreator, expectedDataCreator, expectedRes, actualRes);
    }

    private <T extends FixFsObject> Map<JsonElement, Integer> getJsonEntityCountMap(
            Collection<FixFsObject> children, Map<JsonElement, Integer> jsonEntities) throws IOException {

        for (FixFsObject entry : children) {
            if (!entry.isFile()) {
                throw new RuntimeException(getClass().getName() + " only works with directories with no sub-dirs");
            }
            InputStream content = entry.asFile().getContent();
            fillMapWithJsonFromIS(jsonEntities, content);
        }
        return jsonEntities;
    }

    private void fillMapWithJsonFromIS(Map<JsonElement, Integer> jsonElems, InputStream content) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        String line;
        while ((line = reader.readLine()) != null) {
            JsonElement entity = constructor.construct(line, ignorePaths);
            Integer cnt = jsonElems.get(entity);
            jsonElems.put(entity, cnt == null ? 1 : cnt + 1);
        }
    }

    public Set<String> getIgnorePaths() {
        return ignorePaths;
    }
}
