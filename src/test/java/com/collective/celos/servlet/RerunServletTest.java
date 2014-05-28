package com.collective.celos.servlet;

import com.collective.celos.*;
import com.collective.celos.SlotState.Status;
import com.collective.celos.ScheduledTime;
import org.junit.Assert;
import org.junit.Test;

public class RerunServletTest {

    @Test(expected = IllegalArgumentException.class)
    public void failsOnUndefinedSlot() throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        new RerunServlet().updateSlotToRerun(new SlotID(new WorkflowID("foo"), new ScheduledTime("2014-02-08T20:00Z")), db);
    }
    
    private void failsOnWrongStatusTest(Status status) throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        SlotID id = new SlotID(new WorkflowID("foo"), new ScheduledTime("2014-02-08T20:00Z"));
        SlotState state = new SlotState(id, status);
        db.putSlotState(state);
        new RerunServlet().updateSlotToRerun(id, db);
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
        new RerunServlet().updateSlotToRerun(id, db);
        SlotState dbState = db.getSlotState(id);
        Assert.assertEquals(state.transitionToRerun(), dbState);
    }

    @Test
    public void testRerunWorkflowChained() throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        WorkflowConfiguration cfg = WorkflowConfigurationParserTest.parseFile("dependent-workflows");
        Scheduler scheduler = new Scheduler(cfg, db, 10);
        ScheduledTime time = new ScheduledTime("2014-02-08T20:00Z");

        SlotID slot1 = new SlotID(new WorkflowID("workflow-1"), time);
        SlotID slot2 = new SlotID(new WorkflowID("workflow-2"), time);
        SlotID slot3 = new SlotID(new WorkflowID("workflow-3"), time);
        SlotID slot4 = new SlotID(new WorkflowID("workflow-4"), time);
        SlotID slot5 = new SlotID(new WorkflowID("workflow-5"), time);

        SlotState state1 = new SlotState(slot1, Status.FAILURE);
        db.putSlotState(state1);
        SlotState state2 = new SlotState(slot2, Status.FAILURE);
        db.putSlotState(state2);
        SlotState state3 = new SlotState(slot3, Status.FAILURE);
        db.putSlotState(state3);
        SlotState state4 = new SlotState(slot4, Status.FAILURE);
        db.putSlotState(state4);
        SlotState state5 = new SlotState(slot5, Status.FAILURE);
        db.putSlotState(state5);

        new RerunServlet().rerunWorkflow(scheduler, new SlotID(new WorkflowID("workflow-1"), time), true);
        Assert.assertEquals(db.getSlotState(slot1).getStatus(), Status.WAITING);
        Assert.assertEquals(db.getSlotState(slot2).getStatus(), Status.WAITING);
        Assert.assertEquals(db.getSlotState(slot3).getStatus(), Status.WAITING);
        Assert.assertEquals(db.getSlotState(slot4).getStatus(), Status.WAITING);
        Assert.assertEquals(db.getSlotState(slot5).getStatus(), Status.WAITING);
    }

    @Test
    public void testRerunWorkflowNotChained() throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        WorkflowConfiguration cfg = WorkflowConfigurationParserTest.parseFile("dependent-workflows");
        Scheduler scheduler = new Scheduler(cfg, db, 10);
        ScheduledTime time = new ScheduledTime("2014-02-08T20:00Z");

        SlotID slot1 = new SlotID(new WorkflowID("workflow-1"), time);
        SlotID slot2 = new SlotID(new WorkflowID("workflow-2"), time);
        SlotID slot3 = new SlotID(new WorkflowID("workflow-3"), time);
        SlotID slot4 = new SlotID(new WorkflowID("workflow-4"), time);
        SlotID slot5 = new SlotID(new WorkflowID("workflow-5"), time);

        SlotState state1 = new SlotState(slot1, Status.FAILURE);
        db.putSlotState(state1);
        SlotState state2 = new SlotState(slot2, Status.FAILURE);
        db.putSlotState(state2);
        SlotState state3 = new SlotState(slot3, Status.FAILURE);
        db.putSlotState(state3);
        SlotState state4 = new SlotState(slot4, Status.FAILURE);
        db.putSlotState(state4);
        SlotState state5 = new SlotState(slot5, Status.FAILURE);
        db.putSlotState(state5);

        new RerunServlet().rerunWorkflow(scheduler, new SlotID(new WorkflowID("workflow-1"), time), false);
        Assert.assertEquals(db.getSlotState(slot1).getStatus(), Status.WAITING);
        Assert.assertEquals(db.getSlotState(slot2).getStatus(), Status.FAILURE);
        Assert.assertEquals(db.getSlotState(slot3).getStatus(), Status.FAILURE);
        Assert.assertEquals(db.getSlotState(slot4).getStatus(), Status.FAILURE);
        Assert.assertEquals(db.getSlotState(slot5).getStatus(), Status.FAILURE);
    }


}
