package com.collective.celos.servlet;

import com.collective.celos.*;
import com.collective.celos.SlotState.Status;
import com.collective.celos.ScheduledTime;
import com.collective.celos.server.ServerConfig;
import org.junit.Assert;
import org.junit.Test;

public class RerunServletTest {

    @Test(expected = IllegalArgumentException.class)
    public void failsOnUndefinedSlot() throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        new RerunServlet(new ServerConfig()).updateSlotToRerun(new SlotID(new WorkflowID("foo"), new ScheduledTime("2014-02-08T20:00Z")), db);
    }
    
    @Test(expected = IllegalStateException.class)
    public void failsOnRunningSlot() throws Exception {
        failsOnWrongStatusTest(SlotState.Status.RUNNING);
    }

    @Test(expected = IllegalStateException.class)
    public void failsOnReadySlot() throws Exception {
        failsOnWrongStatusTest(SlotState.Status.READY);
    }

    @Test(expected = IllegalStateException.class)
    public void failsOnWaitingSlot() throws Exception {
        failsOnWrongStatusTest(SlotState.Status.WAITING);
    }

    private void failsOnWrongStatusTest(Status status) throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        SlotID id = new SlotID(new WorkflowID("foo"), new ScheduledTime("2014-02-08T20:00Z"));
        SlotState state = new SlotState(id, status);
        db.putSlotState(state);
        new RerunServlet(new ServerConfig()).updateSlotToRerun(id, db);
    }
    
    @Test
    public void succeedsOnSuccessSlot() throws Exception {
        succeedsOnRightStatusTest(SlotState.Status.SUCCESS);
    }

    @Test
    public void succeedsOnFailureSlot() throws Exception {
        succeedsOnRightStatusTest(SlotState.Status.FAILURE);
    }

    private void succeedsOnRightStatusTest(Status status) throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        SlotID id = new SlotID(new WorkflowID("foo"), new ScheduledTime("2014-02-08T20:00Z"));
        SlotState state = new SlotState(id, status);
        db.putSlotState(state);
        new RerunServlet(new ServerConfig()).updateSlotToRerun(id, db);
        SlotState dbState = db.getSlotState(id);
        Assert.assertEquals(state.transitionToRerun(), dbState);
    }
    
}
