package com.collective.celos.servlet;

import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.MemoryStateDatabase;
import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.StateDatabase;
import com.collective.celos.WorkflowID;

public class RerunServletTest {

    @Test(expected = IllegalArgumentException.class)
    public void failsOnUndefinedSlot() throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        new RerunServlet().updateSlotToRerun(new SlotID(new WorkflowID("foo"), new ScheduledTime("2014-02-08T20:00Z")), db);
    }
    
    @Test
    public void updatesSlotAsExpected() throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        SlotID id = new SlotID(new WorkflowID("foo"), new ScheduledTime("2014-02-08T20:00Z"));
        SlotState state = new SlotState(id, SlotState.Status.SUCCESS);
        db.putSlotState(state);
        new RerunServlet().updateSlotToRerun(id, db);
        SlotState dbState = db.getSlotState(id);
        Assert.assertEquals(state.transitionToRerun(), dbState);
    }
    
}
