package com.collective.celos.servlet;

import com.collective.celos.*;
import com.collective.celos.SlotState.Status;
import com.collective.celos.ScheduledTime;
import org.junit.Assert;
import org.junit.Test;

public class RerunServletTest {

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
        final ScheduledTime current = new ScheduledTime("2014-02-08T20:00Z");
        final WorkflowID workflowID = new WorkflowID("foo");
        SlotID id = new SlotID(workflowID, current);
        SlotState state = new SlotState(id, status);
        db.putSlotState(state);
        db.updateSlotToRerun(id, current);
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
        final ScheduledTime time = new ScheduledTime("2014-02-08T20:00Z");
        SlotID id = new SlotID(new WorkflowID("foo"), time);
        SlotState state = new SlotState(id, status);
        db.putSlotState(state);
        db.updateSlotToRerun(id, time);
        SlotState dbState = db.getSlotState(id);
        Assert.assertEquals(state.transitionToRerun(), dbState);
    }
    
}
