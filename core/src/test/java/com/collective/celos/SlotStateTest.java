package com.collective.celos;

import com.collective.celos.api.ScheduledTime;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SlotStateTest {
    
    private final SlotID id = new SlotID(new WorkflowID("foo"), new ScheduledTime("2013-12-04T19:18Z"));
    
    @Test
    public void transitionsWorkAsExpected() {
        SlotState waiting = new SlotState(id, SlotState.Status.WAITING);
        SlotState ready = new SlotState(id, SlotState.Status.READY);
        Assert.assertEquals(ready, waiting.transitionToReady());
        SlotState running = new SlotState(id, SlotState.Status.RUNNING, "external", 0);
        Assert.assertEquals(running, ready.transitionToRunning("external"));
        SlotState success = new SlotState(id, SlotState.Status.SUCCESS, "external", 0);
        Assert.assertEquals(success, running.transitionToSuccess());        
        SlotState failure = new SlotState(id, SlotState.Status.FAILURE, "external", 0);
        Assert.assertEquals(failure, running.transitionToFailure());        
        SlotState retry = new SlotState(id, SlotState.Status.READY, null, 1);
        Assert.assertEquals(retry, running.transitionToRetry());        
    }
    
    @Test(expected = IllegalStateException.class)
    public void rerunFailsAsExpectedForWaiting() {
        new SlotState(id, SlotState.Status.WAITING).transitionToRerun();
    }
    
    @Test(expected = IllegalStateException.class)
    public void rerunFailsAsExpectedForReady() {
        new SlotState(id, SlotState.Status.READY).transitionToRerun();
    }
    
    @Test(expected = IllegalStateException.class)
    public void rerunFailsAsExpectedForRunning() {
        new SlotState(id, SlotState.Status.RUNNING).transitionToRerun();
    }
    
    @Test
    public void rerunWorksAsExpectedForSuccess() {
        SlotState success = new SlotState(id, SlotState.Status.SUCCESS, "external-ID", 14);
        SlotState expected = new SlotState(id, SlotState.Status.WAITING);
        Assert.assertEquals(expected, success.transitionToRerun());        
    }
    
    @Test
    public void rerunWorksAsExpectedForFailure() {
        SlotState failure = new SlotState(id, SlotState.Status.FAILURE, "external-ID", 14);
        SlotState expected = new SlotState(id, SlotState.Status.WAITING);
        Assert.assertEquals(expected, failure.transitionToRerun());        
    }
    
    @Test
    public void slotStateGetScheduledTimeWorks() {
        ScheduledTime t = new ScheduledTime("2013-11-26T13:00Z");
        SlotState slotState = new SlotState(new SlotID(new WorkflowID("foo"), t), SlotState.Status.READY);
        Assert.assertEquals(t, slotState.getScheduledTime());
    }
    
    @Test
    public void canRoundtripToJSON() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SlotState state1 = new SlotState(id, SlotState.Status.WAITING);
        SlotState state2 = new SlotState(id, SlotState.Status.READY).transitionToRunning("foo-external-ID");
        String json1 = "{\"status\":\"WAITING\",\"externalID\":null,\"retryCount\":0}";
        String json2 = "{\"status\":\"RUNNING\",\"externalID\":\"foo-external-ID\",\"retryCount\":0}";

        Assert.assertEquals(json1, mapper.writeValueAsString(state1.toJSONNode()));
        Assert.assertEquals(json2, mapper.writeValueAsString(state2.toJSONNode()));
        Assert.assertEquals(state1, SlotState.fromJSONNode(id, (ObjectNode) mapper.readTree(json1)));
        Assert.assertEquals(state2, SlotState.fromJSONNode(id, (ObjectNode) mapper.readTree(json2)));
    }

}
