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

import com.collective.celos.*;
import com.collective.celos.pojo.Workflow;
import com.collective.celos.pojo.WorkflowGroup;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.*;


public class WorkflowGroupsServletTest {

    // TODO create new test for groups == null

    final WorkflowGroupsServlet workflowGroupsServlet = new WorkflowGroupsServlet();

    @Test
    public void testProcessWorkflowGroup() throws Exception {

        NavigableSet<ScheduledTime> tileTimes = new TreeSet<>();
        tileTimes.add(new ScheduledTime("2015-09-14T16:00Z"));
        tileTimes.add(new ScheduledTime("2015-09-14T17:00Z"));
        tileTimes.add(new ScheduledTime("2015-09-14T18:00Z"));
        tileTimes.add(new ScheduledTime("2015-09-14T19:00Z"));

        final ArrayList<Workflow> ids = new ArrayList<>();
        ids.add(new Workflow("abcdef"));

        WorkflowGroup xx = workflowGroupsServlet.processWorkflowGroup("dsad", ids, tileTimes, new HashMap<>(), new URL("http://hue"));

        String check = "{\"name\":\"dsad\"," +
                "\"times\":[\"1900\",\"1800\",\"1700\",\"1600\"]," +
                "\"days\":[null,null,null,null]," +
                "\"rows\":[{\"workflowName\":\"abcdef\",\"rows\":[]}]}";

        final String content = Util.JSON_PRETTY.writeValueAsString(xx);
        Assert.assertEquals(Util.MAPPER.readTree(check), Util.MAPPER.readTree(content));
    }

    @Test
    public void testProcessWorkflowGroup2() throws Exception {

        NavigableSet<ScheduledTime> tileTimes = new TreeSet<>();
        tileTimes.add(new ScheduledTime("2015-09-14T16:00Z"));
        tileTimes.add(new ScheduledTime("2015-09-14T17:00Z"));
        tileTimes.add(new ScheduledTime("2015-09-14T18:00Z"));
        tileTimes.add(new ScheduledTime("2015-09-14T19:00Z"));

        final ArrayList<Workflow> ids = new ArrayList<>();
        ids.add(new Workflow("abcdef"));

        Map<Workflow, WorkflowStatus> statuses = new HashMap<>();
        List<SlotState> slotStates = new ArrayList<>();
        slotStates.add(new SlotState(new SlotID(new WorkflowID("abcdef"), new ScheduledTime("2015-09-14T17:00Z")), SlotState.Status.RUNNING));
        WorkflowStatus st = new WorkflowStatus(new WorkflowInfo(null, null), slotStates, false);
        statuses.put(new Workflow("abcdef"), st);
        WorkflowGroup xx = workflowGroupsServlet.processWorkflowGroup("dsad", ids, tileTimes, statuses, new URL("http://hue"));
        String check = "{" +
            "\"name\": \"dsad\"," +
            "\"times\": [\"1900\", \"1800\", \"1700\", \"1600\"]," +
            "\"days\": [null, null, null, null]," +
            "\"rows\": [{" +
                "\"workflowName\": \"abcdef\"," +
                "\"rows\": [{" +
                    "\"status\": \"EMPTY\"," +
                    "\"url\": null," +
                    "\"timestamps\": []," +
                    "\"quantity\": 0" +
                    "}, {" +
                    "\"status\": \"EMPTY\"," +
                    "\"url\": null," +
                    "\"timestamps\": []," +
                    "\"quantity\": 0" +
                    "}, {" +
                    "\"status\": \"RUNNING\"," +
                    "\"url\": null," +
                    "\"timestamps\": [\"2015-09-14T17:00Z\"]," +
                    "\"quantity\": 1" +
                    "}, {" +
                    "\"status\": \"EMPTY\"," +
                    "\"url\": null," +
                    "\"timestamps\": []," +
                    "\"quantity\": 0" +
                    "}]" +
                "}]" +
            "}";

        final String content = Util.JSON_PRETTY.writeValueAsString(xx);
        Assert.assertEquals(Util.MAPPER.readTree(check), Util.MAPPER.readTree(content));
    }

}
