package com.collective.celos.servlet;

import com.collective.celos.*;
import com.collective.celos.SlotState.Status;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class RerunServletTest {

    @Test(expected = IllegalArgumentException.class)
    public void failsOnUndefinedSlot() throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        new RerunServlet().updateSlotToRerun(new SlotID(new WorkflowID("foo"), new ScheduledTime("2014-02-08T20:00Z")), db);
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
    public void multipleSlotsRestart() throws Exception {

        StateDatabase db = new MemoryStateDatabase();
        WorkflowID workflowID = new WorkflowID("foo");

        SlotID slot1 = new SlotID(workflowID, new ScheduledTime("2014-02-08T20:00Z"));
        SlotID slot2 = new SlotID(workflowID, new ScheduledTime("2014-02-08T21:00Z"));
        SlotID slot3 = new SlotID(workflowID, new ScheduledTime("2014-02-08T22:00Z"));
        SlotID slot4 = new SlotID(workflowID, new ScheduledTime("2014-02-08T23:00Z"));

        SlotState state1 = new SlotState(slot1, Status.FAILURE);
        SlotState state2 = new SlotState(slot2, Status.FAILURE);
        SlotState state3 = new SlotState(slot3, Status.FAILURE);
        SlotState state4 = new SlotState(slot4, Status.FAILURE);

        RerunServlet rerunServlet = prepareTestEnvironment(db, workflowID, state1, state2, state3, state4);

        rerunServlet.updateMultipleSlots(slot2.getScheduledTime(), slot3.getScheduledTime(), workflowID);
        Assert.assertEquals(state1, db.getSlotState(slot1));
        Assert.assertEquals(state2.transitionToRerun(), db.getSlotState(slot2));
        Assert.assertEquals(state3, db.getSlotState(slot3));
        Assert.assertEquals(state4, db.getSlotState(slot4));


    }

    private RerunServlet prepareTestEnvironment(StateDatabase db, WorkflowID workflowID, SlotState state1, SlotState state2, SlotState state3, SlotState state4) throws Exception {
        db.putSlotState(state1);
        db.putSlotState(state2);
        db.putSlotState(state3);
        db.putSlotState(state4);

        CronSchedule schedule = new HourlySchedule();
        AlwaysTrigger trigger = new AlwaysTrigger();

        MockExternalService srv = new MockExternalService(new MockExternalService.MockExternalStatusRunning());

        Workflow wf = new Workflow(workflowID, schedule, new SerialSchedulingStrategy(1), trigger, srv, 1, Workflow.DEFAULT_START_TIME);

        WorkflowConfiguration cfg = new WorkflowConfiguration();
        cfg.addWorkflow(wf, "");

        Scheduler sch = new Scheduler(cfg, db, 1);

        RerunServlet rerunServlet = Mockito.spy(new RerunServlet());

        Mockito.doReturn(sch).when(rerunServlet).getOrCreateCachedScheduler();
        return rerunServlet;
    }


}
