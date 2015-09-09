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

import com.collective.celos.ci.Utils;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by akonopko on 25.01.15.
 */
public class FixTableToJsonFileConverterTest {

    @Test
    public void testFixTableToJsonFileConverter() throws Exception {
        FixTableToJsonFileConverter converter = new FixTableToJsonFileConverter();
        List<FixTable.FixRow> fixRows = Lists.newArrayList();

        Map<String, String> map = Maps.newHashMap();
        map.put("col1", "val1");
        map.put("col2", "val2");

        Map<String, String> map2 = Maps.newHashMap();
        map2.put("col1", "val11");
        map2.put("col2", "val22");

        FixTable.FixRow row1 = new FixTable.FixRow(map);
        FixTable.FixRow row2 = new FixTable.FixRow(map2);
        fixRows.add(row1);
        fixRows.add(row2);
        FixTable fixTable = new FixTable(Lists.newArrayList("col1", "col2"), fixRows);
        FixFile fixFile = converter.convert(null, fixTable);

        String tableStr = IOUtils.toString(fixFile.getContent());
        String expectedStr =
                "{\"col1\":\"val1\",\"col2\":\"val2\"}\n" +
                "{\"col1\":\"val11\",\"col2\":\"val22\"}";

        Map<JsonElement, Integer> expected = Utils.fillMapWithJsonFromIS(IOUtils.toInputStream(expectedStr));
        Map<JsonElement, Integer> result = Utils.fillMapWithJsonFromIS(IOUtils.toInputStream(tableStr));
        Assert.assertEquals(expected, result);
    }

}
