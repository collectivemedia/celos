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
import com.collective.celos.pojo.WorkflowGroup;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.*;


public class WorkflowsServletTest {

    // TODO create new test for groups == null

    public final static ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testProcessWorkflowGroup() throws Exception {
        CelosClient client = Mockito.mock(CelosClient.class);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

        NavigableSet<ScheduledTime> tileTimes = new TreeSet<>();
        tileTimes.add(new ScheduledTime("2015-09-14T16:00Z"));
        tileTimes.add(new ScheduledTime("2015-09-14T17:00Z"));
        tileTimes.add(new ScheduledTime("2015-09-14T18:00Z"));
        tileTimes.add(new ScheduledTime("2015-09-14T19:00Z"));

        final ArrayList<WorkflowID> ids = new ArrayList<>();
        ids.add(new WorkflowID("abcdef"));

        WorkflowGroup xx = new WorkflowGroupsServlet().processWorkflowGroup("dsad", ids, tileTimes, new HashMap<>(), new URL("http://hue"));

        String check = "{\"name\":\"dsad\",\"times\":[\"1900\",\"1800\",\"1700\",\"1600\"],\"days\":[null,null,null,null],\"rows\":[{\"workflowName\":\"abcdef\",\"rows\":[]}]}";

        final String content = Util.JSON_PRETTY.writeValueAsString(xx);
        System.out.println("content =");
        System.out.println(content);
        Assert.assertEquals(MAPPER.readTree(check), MAPPER.readTree(content));
    }

}
