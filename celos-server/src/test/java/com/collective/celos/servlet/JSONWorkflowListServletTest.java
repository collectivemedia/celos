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
package com.collective.celos.servlet;

import com.collective.celos.Util;
import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.WorkflowConfiguration;
import com.collective.celos.WorkflowConfigurationParserTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONWorkflowListServletTest {

    @Test
    public void jsonCorrectlyProduced() throws Exception {
        WorkflowConfiguration cfg =
                WorkflowConfigurationParserTest.parseDir("json-workflow-list-servlet-test");
        ArrayNode list = Util.MAPPER.createArrayNode();
        list.add(new String("workflow-1"));
        list.add(new String("workflow-2"));
        ObjectNode obj = Util.MAPPER.createObjectNode();
        obj.put("ids", list);
        Assert.assertEquals(obj, new JSONWorkflowListServlet().createJSONObject(cfg));
    }
    
}
