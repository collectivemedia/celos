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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.collective.celos.*;
import com.collective.celos.ui.ConfigServlet;
import com.collective.celos.ui.UIConfiguration;
import com.collective.celos.ui.WorkflowGroup;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.*;
import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import org.mockito.Mockito;

import javax.servlet.ServletContext;

public class ConfigServletTest {

    protected final static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testUIConfig() throws Exception {
        String input = "{\n" +
                "  \"groups\": [\n" +
                "    {\n" +
                "      \"name\": \"Group1\",\n" +
                "      \"workflows\": [\n" +
                "        \"Group1-1\",\n" +
                "        \"Group1-2\",\n" +
                "        \"Group1-3\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"Group2\",\n" +
                "      \"workflows\": [\n" +
                "        \"Group2-1\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"Group3\",\n" +
                "      \"workflows\": [\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";

        final Set<WorkflowID> workflowIDs = new TreeSet<>();
        workflowIDs.add(new WorkflowID("Group2-1"));
        workflowIDs.add(new WorkflowID("abiurvalg"));
        final String result = ConfigServlet.processGet(workflowIDs, Optional.of(input));
        System.out.println(result);
        final JsonNode jsonNode = mapper.readTree(result);
        Assert.assertEquals(4, jsonNode.get("rows").size());
    }

}
