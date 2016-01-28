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
package com.collective.celos;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.google.common.collect.Sets;

/**
 * Created by akonopko on 03.02.15.
 */
public class CelosClientTest {

    @Test (expected = UnknownHostException.class)
    public void testCelosClientFailsNoServer() throws Exception {
        CelosClient celosClient = new CelosClient(URI.create("http://thereisnosuchhost:1234"));
        celosClient.getWorkflowList();
    }

    @Test (expected = UnknownHostException.class)
    public void testCheckStatusFailsNoServer() throws Exception {
        CelosClient celosClient = new CelosClient(URI.create("http://thereisnosuchhost:1234"));
        celosClient.checkStatus();
    }


    @Test
    public void testParseWorkflowStatusesMap() throws IOException {

        String str = "" +
                "{\n" +
                "\"info\": { \"url\": \"http://myurl\", \"contacts\": [ { \"name\": \"John Doe\", \"email\": \"John.Doe@Gmail.Com\"} ] },\n" +
                "\"paused\": \"true\",\n" +
                "\"slots\": [\n" +
                "  {\n" +
                "    \"time\" : \"2014-10-27T14:00:00.000Z\",\n" +
                "    \"status\" : \"SUCCESS\",\n" +
                "    \"externalID\" : \"0029532-141007123109603-oozie-oozi-W\",\n" +
                "    \"retryCount\" : 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"time\" : \"2014-10-27T15:00:00.000Z\",\n" +
                "    \"status\" : \"FAILURE\",\n" +
                "    \"externalID\" : \"0029595-141007123109603-oozie-oozi-W\",\n" +
                "    \"retryCount\" : 2\n" +
                "   }" +
                "   ]" +
                "}";

        WorkflowID workflowID = new WorkflowID("123");

        WorkflowStatus workflowStatus = new CelosClient(URI.create("localhost")).parseWorkflowStatus(workflowID, new ByteArrayInputStream(str.getBytes()));
        Assert.assertTrue(workflowStatus.isPaused());

        List<SlotState> result = workflowStatus.getSlotStates();
        Assert.assertEquals(result.size(), 2);

        ScheduledTime time1 = new ScheduledTime("2014-10-27T14:00:00.000Z");
        ScheduledTime time2 = new ScheduledTime("2014-10-27T15:00:00.000Z");

        SlotState val1 = new SlotState(new SlotID(workflowID, time1), SlotState.Status.SUCCESS, "0029532-141007123109603-oozie-oozi-W", 0);
        SlotState val2 = new SlotState(new SlotID(workflowID, time2), SlotState.Status.FAILURE, "0029595-141007123109603-oozie-oozi-W", 2);

        Assert.assertEquals(Sets.newHashSet(result), Sets.newHashSet(val1, val2));
    }


    @Test
    public void testParseWorkflowIdsList() throws IOException {
        String str =
                "{\n" +
                "  \"ids\" : [ \"GC-export-profiles\", \"GrandCentral-01-harmony\"]" +
                "}";

        Set<WorkflowID> result = new CelosClient(URI.create("localhost")).parseWorkflowIdsList(new ByteArrayInputStream(str.getBytes()));
        Assert.assertEquals(result, Sets.newHashSet(new WorkflowID("GrandCentral-01-harmony"), new WorkflowID("GC-export-profiles")));
    }

}
