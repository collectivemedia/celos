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
package com.collective.celos.ci.testing.fixtures.convert;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.AbstractFixObjectConverter;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

/**
 * Created by akonopko on 23.01.15.
 */
public class JsonExpandConverter extends AbstractFixObjectConverter<FixFile, FixFile> {

    private final JsonParser jsonParser = new JsonParser();
    private final Gson gson = new GsonBuilder().create();
    private final Set<String> expandFields;

    public JsonExpandConverter(Set<String> expandFields) {
        this.expandFields = expandFields;
    }

    @Override
    public FixFile convert(TestRun tr, FixFile ff) throws Exception {

        List<String> stringList = Lists.newArrayList();

        BufferedReader reader = new BufferedReader(new InputStreamReader(ff.getContent()));
        String line;
        while ((line = reader.readLine()) != null) {
            JsonElement jsonElement = jsonParser.parse(line);
            for (String field : expandFields) {
                try {
                    String strField = jsonElement.getAsJsonObject().get(field).getAsString();
                    JsonElement fieldElem = jsonParser.parse(strField);
                    jsonElement.getAsJsonObject().add(field, fieldElem);
                } catch(Exception e) {
                    throw new Exception("Error caused in line: " + line + " trying to expand field: " + field, e); 
                }
            }
            stringList.add(gson.toJson(jsonElement));
        }

        return new FixFile(IOUtils.toInputStream(StringUtils.join(stringList,"\n")));
    }
}
