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

import java.util.LinkedList;
import java.util.List;

import java.time.ZonedDateTime;
import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONWorkflowServletTest {

    @Test
    public void jsonCorrectlyProduced() throws Exception {
        ZonedDateTime t1 = ZonedDateTime.parse("2013-12-20T20:00Z");
        ZonedDateTime t2 = ZonedDateTime.parse("2013-12-20T21:00Z");
        WorkflowID wfID = new WorkflowID("foobar");
        SlotState s1 = new SlotState(new SlotID(wfID, t1), SlotState.Status.READY);
        SlotState s2 = new SlotState(new SlotID(wfID, t2), SlotState.Status.RUNNING, "external-ID", 4);
        List<SlotState> states = new LinkedList<SlotState>();
        states.add(s1);
        states.add(s2);
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put(t1.toString(), s1.toJSONNode());
        node.put(t2.toString(), s2.toJSONNode());
        
        Assert.assertEquals(node, new JSONWorkflowServlet().createJSONObject(states));
    }
    
}
