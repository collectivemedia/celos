package com.collective.celos;

import org.junit.Assert;
import org.junit.Test;

public class SlotStateTest {
    
    @Test
    public void transitionsWorkAsExpected() {
        SlotID id = new SlotID(new WorkflowID("foo"), new ScheduledTime("2013-12-04T19:18Z"));
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

    @Test
    public void slotStateGetScheduledTimeWorks() {
        ScheduledTime t = new ScheduledTime("2013-11-26T13:00Z");
        SlotState slotState = new SlotState(new SlotID(new WorkflowID("foo"), t), SlotState.Status.READY);
        Assert.assertEquals(t, slotState.getScheduledTime());
    }

}
