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

import com.collective.celos.CelosClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;


/**
 * Renders the UI HTML.
 */
public class UIActionServletTest {

    public final static ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testParsesOk() throws Exception {
        CelosClient client = Mockito.mock(CelosClient.class);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

        final String ss = "{\n" +
                "  \"type\" : \"AndTrigger\",\n" +
                "  \"ready\" : false,\n" +
                "  \"description\" : \"Not all nested triggers are ready\",\n" +
                "  \"subStatuses\" : [ {\n" +
                "    \"type\" : \"DelayTrigger\",\n" +
                "    \"ready\" : false,\n" +
                "    \"description\" : \"Delayed until 2015-09-14T16:00:00.000Z\",\n" +
                "    \"subStatuses\" : [ ]\n" +
                "  }, {\n" +
                "    \"type\" : \"HDFSCheckTrigger\",\n" +
                "    \"ready\" : true,\n" +
                "    \"description\" : \"HDFS path hdfs://nameservice1/logs/dc3/2015-09-14/1500 is ready\",\n" +
                "    \"subStatuses\" : [ ]\n" +
                "  } ]\n" +
                "}";

        Mockito.when(client.getTriggerStatusAsText(Mockito.anyString(), Mockito.anyString()))
               .thenReturn(MAPPER.readTree(ss));
//        Mockito.when(req.getParameter(TriggerStatusServlet.ID_PARAM))
//                .thenReturn("workflow-1");
//        Mockito.when(req.getParameter(TriggerStatusServlet.TIME_PARAM))
//                .thenReturn("2015-09-13T13:00Z,2015-09-13T13:00Z");

//        ArrayNode xx = TriggerStatusServlet.getJsonNodes(req, client);

//        Assert.assertEquals(xx.size(), 2);
//        Assert.assertNotNull(MAPPER.writeValueAsString(xx));

    }

}
