package com.collective.celos;

import junit.framework.Assert;

import org.junit.Test;

public class MemoryStateDatabaseTest {

    @Test
    public void getAndPutWorks() throws Exception {
        MemoryStateDatabase db = new MemoryStateDatabase();
        SlotID slotID = new SlotID(new WorkflowID("foo"), new ScheduledTimeImpl("2013-11-27T14:50Z"));
        Assert.assertEquals(null, db.getSlotState(slotID));
        SlotState state = new SlotState(slotID, SlotState.Status.READY);
        db.putSlotState(state);
        Assert.assertEquals(state, db.getSlotState(slotID));
        Assert.assertEquals(null, db.getSlotState(new SlotID(new WorkflowID("bar"), new ScheduledTimeImpl("2013-11-27T14:50Z"))));
    }
    
}
