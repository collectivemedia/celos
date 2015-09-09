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
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by akonopko on 22.01.15.
 */
public class FixTableToJsonFileConverter extends AbstractFixObjectConverter<FixTable,FixFile> {

    private final Gson gson = new GsonBuilder().create();

    @Override
    public FixFile convert(TestRun tr, FixTable ff) throws Exception {
        List<String> jsonStrs = Lists.newArrayList();
        for (FixTable.FixRow fr : ff.getRows()) {
            jsonStrs.add(gson.toJson(fr.getCells()));
        }
        return new FixFile(IOUtils.toInputStream(StringUtils.join(jsonStrs, "\n")));
    }
}
