package com.collective.celos.servlet;

import com.collective.celos.*;
import org.junit.Assert;
import org.junit.Test;


public class RerunServletTest {

    @Test(expected = IllegalArgumentException.class)
    public void failsOnUndefinedSlot() throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        new RerunServlet().updateSlotToRerun(new SlotID(new WorkflowID("foo"), new ScheduledTimeImpl("2014-02-08T20:00Z")), db);
    }
    
    @Test
    public void updatesSlotAsExpected() throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        SlotID id = new SlotID(new WorkflowID("foo"), new ScheduledTimeImpl("2014-02-08T20:00Z"));
        SlotState state = new SlotState(id, SlotState.Status.SUCCESS);
        db.putSlotState(state);
        new RerunServlet().updateSlotToRerun(id, db);
        SlotState dbState = db.getSlotState(id);
        Assert.assertEquals(state.transitionToRerun(), dbState);
    }
    
}
