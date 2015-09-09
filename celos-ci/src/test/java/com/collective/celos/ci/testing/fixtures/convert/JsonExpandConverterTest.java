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
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by akonopko on 24.01.15.
 */
public class JsonExpandConverterTest {

    @Test
    public void testJsonExpandConverterNoConversion() throws Exception {
        JsonExpandConverter converter = new JsonExpandConverter(Sets.<String>newHashSet());
        String jsonExample =
                "{\"id\":\"134f50faa804d30\",\"change\":\"{\\\"daystamp\\\":\\\"20140901\\\",\\\"context\\\":\\\"none\\\"}\",\"origin\":\"dc\"}\n" +
                "{\"id\":\"134f50faa804d31\",\"change\":\"{\\\"daystamp\\\":\\\"20140902\\\",\\\"context\\\":\\\"none\\\"}\",\"origin\":\"dc\"}";

        InputStream inputStream = IOUtils.toInputStream(jsonExample);
        FixFile expanded = converter.convert(null, new FixFile(inputStream));
        String expandedStr = IOUtils.toString(expanded.getContent());

        Assert.assertEquals(jsonExample, expandedStr);
    }

    @Test
    public void testJsonExpandConverter() throws Exception {
        JsonExpandConverter converter = new JsonExpandConverter(Sets.newHashSet("change"));
        String jsonExample =
                "{\"id\":\"134f50faa804d30\",\"change\":\"{\\\"daystamp\\\":\\\"20140901\\\",\\\"context\\\":\\\"none\\\"}\",\"origin\":\"dc\"}\n" +
                "{\"id\":\"134f50faa804d31\",\"change\":\"{\\\"daystamp\\\":\\\"20140902\\\",\\\"context\\\":\\\"none\\\"}\",\"origin\":\"dc\"}";

        InputStream inputStream = IOUtils.toInputStream(jsonExample);
        FixFile expanded = converter.convert(null, new FixFile(inputStream));
        String expandedStr = IOUtils.toString(expanded.getContent());

        String expectedStr =
                "{\"id\":\"134f50faa804d30\",\"change\":{\"daystamp\":\"20140901\",\"context\":\"none\"},\"origin\":\"dc\"}\n" +
                "{\"id\":\"134f50faa804d31\",\"change\":{\"daystamp\":\"20140902\",\"context\":\"none\"},\"origin\":\"dc\"}";

        Map<JsonElement, Integer> expected = Utils.fillMapWithJsonFromIS(IOUtils.toInputStream(expectedStr));
        Map<JsonElement, Integer> result = Utils.fillMapWithJsonFromIS(IOUtils.toInputStream(expandedStr));
        Assert.assertEquals(expected, result);

    }

}
