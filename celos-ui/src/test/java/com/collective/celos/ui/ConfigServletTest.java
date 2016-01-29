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
package com.collective.celos.ui;

import java.util.*;

import com.collective.celos.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

public class ConfigServletTest {

    protected final static ObjectMapper mapper = new ObjectMapper();

    final String check = "{" +
            "  \"rows\" : [ {" +
            "    \"name\" : \"Group1\"," +
            "    \"times\" : [ ]," +
            "    \"days\" : [ ]," +
            "    \"rows\" : [ {" +
            "      \"workflowName\" : \"Group1-1\"," +
            "      \"rows\" : [ ]" +
            "    }, {" +
            "      \"workflowName\" : \"Group1-2\"," +
            "      \"rows\" : [ ]" +
            "    }, {" +
            "      \"workflowName\" : \"Group1-3\"," +
            "      \"rows\" : [ ]" +
            "    } ]" +
            "  }, {" +
            "    \"name\" : \"Group2\"," +
            "    \"times\" : [ ]," +
            "    \"days\" : [ ]," +
            "    \"rows\" : [ {" +
            "      \"workflowName\" : \"Group2-1\"," +
            "      \"rows\" : [ ]" +
            "    } ]" +
            "  }, {" +
            "    \"name\" : \"Group3\"," +
            "    \"times\" : [ ]," +
            "    \"days\" : [ ]," +
            "    \"rows\" : [ ]" +
            "  }, {" +
            "    \"name\" : \"Unlisted workflows\"," +
            "    \"times\" : [ ]," +
            "    \"days\" : [ ]," +
            "    \"rows\" : [ {" +
            "      \"workflowName\" : \"Group2-1\"," +
            "      \"rows\" : [ ]" +
            "    } ]" +
            "  } ]" +
            "}";

    final String input = "{" +
            "  \"groups\": [" +
            "    {" +
            "      \"name\": \"Group1\"," +
            "      \"workflows\": [" +
            "        \"Group1-1\"," +
            "        \"Group1-2\"," +
            "        \"Group1-3\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"name\": \"Group2\"," +
            "      \"workflows\": [" +
            "        \"Group2-1\"" +
            "      ]" +
            "    }," +
            "    {" +
            "      \"name\": \"Group3\"," +
            "      \"workflows\": [" +
            "      ]" +
            "    }" +
            "  ]" +
            "}";

    @Test
    public void testUIConfig() throws Exception {

        final List<String> workflowIDs = new ArrayList<>();
        workflowIDs.add("Group2-1");
        workflowIDs.add("abiurvalg");
        final String result = ConfigServlet.processGet(workflowIDs, Optional.of(input));
        final JsonNode jsonNode = mapper.readTree(result);
        Assert.assertEquals(4, jsonNode.get("rows").size());

        Assert.assertEquals(jsonNode, mapper.readTree(check));

    }

}
