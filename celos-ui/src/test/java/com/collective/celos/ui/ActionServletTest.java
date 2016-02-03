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
import com.collective.celos.ScheduledTime;
import com.collective.celos.Util;
import com.collective.celos.WorkflowID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;


/**
 * Renders the UI HTML.
 */
public class ActionServletTest {

    @Test
    public void testProcessPost() throws Exception {
        CelosClient client = Mockito.mock(CelosClient.class);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

        final String ss = "{\n" +
                "\"action\": \"kill\",\n" +
                "\"slots\": [{\n" +
                "\"ts\": \"2016-01-28T09:00Z\",\n" +
                "\"workflowName\": \"flume-tmp-file-closer\",\n" +
                "\"breadcrumbs\": [\"rows\", 0, \"rows\", 2, \"rows\", 11, \"timestamps\", 0]\n" +
                "}, {\n" +
                "\"ts\": \"2016-01-28T09:15Z\",\n" +
                "\"workflowName\": \"flume-tmp-file-closer\",\n" +
                "\"breadcrumbs\": [\"rows\", 0, \"rows\", 2, \"rows\", 11, \"timestamps\", 1]\n" +
                "}, {\n" +
                "\"ts\": \"2016-01-28T09:30Z\",\n" +
                "\"workflowName\": \"flume-tmp-file-closer\",\n" +
                "\"breadcrumbs\": [\"rows\", 0, \"rows\", 2, \"rows\", 11, \"timestamps\", 2]\n" +
                "}, {\n" +
                "\"ts\": \"2016-01-28T09:45Z\",\n" +
                "\"workflowName\": \"flume-tmp-file-closer\",\n" +
                "\"breadcrumbs\": [\"rows\", 0, \"rows\", 2, \"rows\", 11, \"timestamps\", 3]\n" +
                "}]\n" +
                "}";

        new ActionServlet().processPost(client, "kill", Util.MAPPER.readTree(ss));

        Mockito.verify(client, Mockito.atLeastOnce())
                .kill(new WorkflowID("flume-tmp-file-closer"), new ScheduledTime("2016-01-28T09:00Z"));

        Mockito.verify(client, Mockito.never())
                .rerunSlot(Mockito.any(WorkflowID.class), Mockito.any(ScheduledTime.class));

    }

}
