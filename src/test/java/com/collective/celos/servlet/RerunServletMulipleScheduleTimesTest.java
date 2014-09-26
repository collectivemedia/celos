package com.collective.celos.servlet;

import com.collective.celos.*;
import com.collective.celos.SlotState.Status;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RerunServletMulipleScheduleTimesTest {

    private StateDatabase db = new MemoryStateDatabase();
    private WorkflowID workflowID = new WorkflowID("foo");

    private SlotID slot1 = new SlotID(workflowID, new ScheduledTime("2014-02-08T20:00Z"));
    private SlotID slot2 = new SlotID(workflowID, new ScheduledTime("2014-02-08T21:00Z"));
    private SlotID slot3 = new SlotID(workflowID, new ScheduledTime("2014-02-08T22:00Z"));
    private SlotID slot4 = new SlotID(workflowID, new ScheduledTime("2014-02-08T23:00Z"));

    private SlotState slot1Failed = new SlotState(slot1, Status.FAILURE);
    private SlotState slot1Waiting = new SlotState(slot1, Status.WAITING);
    private SlotState slot2Failed = new SlotState(slot2, Status.FAILURE);
    private SlotState slot2Waiting = new SlotState(slot2, Status.WAITING);
    private SlotState slot3Failed = new SlotState(slot3, Status.FAILURE);
    private SlotState slot3Waiting = new SlotState(slot3, Status.WAITING);
    private SlotState slot4Failed = new SlotState(slot4, Status.FAILURE);
    private SlotState slot4Waiting = new SlotState(slot4, Status.WAITING);

    private RerunServlet rerunServlet;

    @Before
    public void prepareEnv() throws Exception {

        CronSchedule schedule = new HourlySchedule();
        AlwaysTrigger trigger = new AlwaysTrigger();

        MockExternalService srv = new MockExternalService(new MockExternalService.MockExternalStatusRunning());

        Workflow wf = new Workflow(workflowID, schedule, new SerialSchedulingStrategy(1), trigger, srv, 1, Workflow.DEFAULT_START_TIME);

        WorkflowConfiguration cfg = new WorkflowConfiguration();
        cfg.addWorkflow(wf, "");

        Scheduler sch = new Scheduler(cfg, db, 1);

        RerunServlet rerunServlet = Mockito.spy(new RerunServlet());

        Mockito.doReturn(sch).when(rerunServlet).getOrCreateCachedScheduler();
        this.rerunServlet = rerunServlet;
    }

    @Test
    public void onlyOneSlotRestarts() throws Exception {

        db.putSlotState(slot1Failed);
        db.putSlotState(slot2Failed);
        db.putSlotState(slot3Failed);
        db.putSlotState(slot4Failed);


        rerunServlet.updateMultipleSlots(slot2.getScheduledTime(), slot3.getScheduledTime(), workflowID);

        Assert.assertEquals(slot1Failed, db.getSlotState(slot1));
        Assert.assertEquals(slot2Waiting, db.getSlotState(slot2));
        Assert.assertEquals(slot3Failed, db.getSlotState(slot3));
        Assert.assertEquals(slot4Failed, db.getSlotState(slot4));
    }

    @Test
    public void threeSlotRestarts() throws Exception {

        db.putSlotState(slot1Failed);
        db.putSlotState(slot2Failed);
        db.putSlotState(slot3Failed);
        db.putSlotState(slot4Failed);


        rerunServlet.updateMultipleSlots(slot1.getScheduledTime(), slot4.getScheduledTime(), workflowID);

        Assert.assertEquals(slot1Waiting, db.getSlotState(slot1));
        Assert.assertEquals(slot2Waiting, db.getSlotState(slot2));
        Assert.assertEquals(slot3Waiting, db.getSlotState(slot3));
        Assert.assertEquals(slot4Failed, db.getSlotState(slot4));
    }

    @Test
    public void allSlotRestarts() throws Exception {

        db.putSlotState(slot1Failed);
        db.putSlotState(slot2Failed);
        db.putSlotState(slot3Failed);
        db.putSlotState(slot4Failed);


        rerunServlet.updateMultipleSlots(slot1.getScheduledTime(), slot4.getScheduledTime().plusSeconds(1), workflowID);

        Assert.assertEquals(slot1Waiting, db.getSlotState(slot1));
        Assert.assertEquals(slot2Waiting, db.getSlotState(slot2));
        Assert.assertEquals(slot3Waiting, db.getSlotState(slot3));
        Assert.assertEquals(slot4Waiting, db.getSlotState(slot4));
    }

    @Test
    public void noneSlotRestarts() throws Exception {

        db.putSlotState(slot1Failed);
        db.putSlotState(slot2Failed);
        db.putSlotState(slot3Failed);
        db.putSlotState(slot4Failed);


        rerunServlet.updateMultipleSlots(slot1.getScheduledTime(), slot1.getScheduledTime(), workflowID);

        Assert.assertEquals(slot1Failed, db.getSlotState(slot1));
        Assert.assertEquals(slot2Failed, db.getSlotState(slot2));
        Assert.assertEquals(slot3Failed, db.getSlotState(slot3));
        Assert.assertEquals(slot4Failed, db.getSlotState(slot4));
    }


}
