package com.collective.celos.servlet;

import java.util.LinkedList;
import java.util.List;

import com.collective.celos.ScheduledTimeImpl;
import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.api.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONWorkflowServletTest {

    @Test
    public void jsonCorrectlyProduced() throws Exception {
        ScheduledTime t1 = new ScheduledTimeImpl("2013-12-20T20:00:00.000Z");
        ScheduledTime t2 = new ScheduledTimeImpl("2013-12-20T21:00:00.000Z");
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
