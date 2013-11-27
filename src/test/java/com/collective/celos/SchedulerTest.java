package com.collective.celos;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

/**
 * TODO: test exception handling and logging
 * TODO: more fine-grained tests like multipleSlotsTest
 */
public class SchedulerTest {

    @Test(expected=IllegalArgumentException.class)
    public void slidingWindowHoursPositive1() {
        new Scheduler(0);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void slidingWindowHoursPositive2() {
        new Scheduler(-23);
    }

    @Test
    public void slidingWindowSizeWorks() {
        ScheduledTime t = new ScheduledTime("2013-11-26T20:00Z");
        int hours = 5;
        Assert.assertEquals(new Scheduler(hours).getStartTime(t), new ScheduledTime("2013-11-26T15:00Z"));
    }
    
    /**
     * Create a workflow with a hourly schedule and an always trigger.
     * 
     * Step the workflow a single time.
     * 
     * Ensure that all hourly slots have been changed to ready.
     */
    @Test
    public void updatesWaitingSlotsToReady() throws Exception {
        WorkflowID wfID1 = new WorkflowID("wf1");
        Schedule sch1 = new HourlySchedule();
        SchedulingStrategy str1 = new SerialSchedulingStrategy();
        Trigger tr1 = new AlwaysTrigger();
        ExternalService srv1 = new MockExternalService(new MockExternalService.MockExternalStatusRunning());
        Workflow wf1 = new Workflow(wfID1, sch1, str1, tr1, srv1);
        
        Set<Workflow> workflows = new HashSet<Workflow>();
        workflows.add(wf1);
        WorkflowConfiguration cfg = new WorkflowConfiguration(workflows);
        
        MemoryStateDatabase db = new MemoryStateDatabase();

        int slidingWindowHours = 24;
        DateTime current = DateTime.parse("2013-11-27T15:01Z");
        DateTime currentFullHour = Util.toFullHour(current);
        
        Scheduler sched = new Scheduler(slidingWindowHours);
        sched.step(new ScheduledTime(current), cfg, db);
        
        Assert.assertEquals(slidingWindowHours, db.size());
        
        for (int i = 0; i < slidingWindowHours; i++) {
            SlotID id = new SlotID(wfID1, new ScheduledTime(currentFullHour.minusHours(i)));
            SlotState state = db.getSlotState(id);
            if (state == null) {
                throw new AssertionError("Slot " + id + " not found.");
            }
            Assert.assertEquals(SlotState.Status.READY, state.getStatus());
        }
        
    }
    
    /**
     * Create a workflow with a hourly schedule and a never trigger.
     * 
     * Step the workflow a single time.
     * 
     * Ensure that no hourly slots have been changed to ready, and in fact,
     * that no slots have been updated in the database.
     */
    @Test
    public void doesNotUpdateWaitingSlotsToReadyWhenNoDataAvailability() throws Exception {
        WorkflowID wfID1 = new WorkflowID("wf1");
        Schedule sch1 = new HourlySchedule();
        SchedulingStrategy str1 = new SerialSchedulingStrategy();
        Trigger tr1 = new NeverTrigger();
        ExternalService srv1 = new MockExternalService(new MockExternalService.MockExternalStatusRunning());
        Workflow wf1 = new Workflow(wfID1, sch1, str1, tr1, srv1);
        
        Set<Workflow> workflows = new HashSet<Workflow>();
        workflows.add(wf1);
        WorkflowConfiguration cfg = new WorkflowConfiguration(workflows);
        
        MemoryStateDatabase db = new MemoryStateDatabase();

        int slidingWindowHours = 24;
        DateTime current = DateTime.parse("2013-11-27T15:01Z");
        DateTime currentFullHour = Util.toFullHour(current);
        
        Scheduler sched = new Scheduler(slidingWindowHours);
        sched.step(new ScheduledTime(current), cfg, db);
        
        Assert.assertEquals(0, db.size());
    }
    
    /**
     * Creates running slots in memory database, with mock external service
     * that always says the external jobs are still running.
     * 
     * Makes sure that the slots are still marked as running after step.
     */
    @Test
    public void leavesRunningSlotsAsIsIfStillRunning() throws Exception {
        runningSlotUtil(new MockExternalService.MockExternalStatusRunning(), SlotState.Status.RUNNING);
    }
    
    /**
     * Creates running slots in memory database, with mock external service
     * that always says the external jobs are successful.
     * 
     * Makes sure that the slots are marked as successful after step.
     */
    @Test
    public void marksRunningSlotsAsSuccessfulIfExternalStatusIsSuccess() throws Exception {
        runningSlotUtil(new MockExternalService.MockExternalStatusSuccess(), SlotState.Status.SUCCESS);
    }
    
    /**
     * Creates running slots in memory database, with mock external service
     * that always says the external jobs are failed.
     * 
     * Makes sure that the slots are marked as failed after step.
     */
    @Test
    public void marksRunningSlotsAsFailedIfExternalStatusIsFailure() throws Exception {
        runningSlotUtil(new MockExternalService.MockExternalStatusFailure(), SlotState.Status.FAILURE);
    }

    private void runningSlotUtil(ExternalStatus externalStatus, SlotState.Status expectedSlotStatus) throws Exception, AssertionError {
        WorkflowID wfID1 = new WorkflowID("wf1");
        Schedule sch1 = new HourlySchedule();
        SchedulingStrategy str1 = new SerialSchedulingStrategy();
        Trigger tr1 = new AlwaysTrigger();
        ExternalService srv1 = new MockExternalService(externalStatus);
        Workflow wf1 = new Workflow(wfID1, sch1, str1, tr1, srv1);
        
        Set<Workflow> workflows = new HashSet<Workflow>();
        workflows.add(wf1);
        WorkflowConfiguration cfg = new WorkflowConfiguration(workflows);
        
        MemoryStateDatabase db = new MemoryStateDatabase();

        int slidingWindowHours = 24;
        DateTime current = DateTime.parse("2013-11-27T15:01Z");
        DateTime currentFullHour = Util.toFullHour(current);

        for (int i = 0; i < slidingWindowHours; i++) {
            SlotID id = new SlotID(wfID1, new ScheduledTime(currentFullHour.minusHours(i)));
            SlotState state = new SlotState(id, SlotState.Status.READY).transitionToRunning("fake-external-ID");
            db.putSlotState(state);
        }
        
        Scheduler sched = new Scheduler(slidingWindowHours);
        sched.step(new ScheduledTime(current), cfg, db);
        
        Assert.assertEquals(slidingWindowHours, db.size());
        
        for (int i = 0; i < slidingWindowHours; i++) {
            SlotID id = new SlotID(wfID1, new ScheduledTime(currentFullHour.minusHours(i)));
            SlotState state = db.getSlotState(id);
            if (state == null) {
                throw new AssertionError("Slot " + id + " not found.");
            }
            Assert.assertEquals(expectedSlotStatus, state.getStatus());
        }
    }

    /**
     * Creates ready slots in memory database.
     * 
     * Uses trivial scheduling strategy so all ready slots will be used.
     * 
     * Makes sure all are running after step, and have been submitted to external service.
     */
    @Test
    public void updatesReadySlotsToRunningAndSubmitsThemToExternalSystem() throws Exception, AssertionError {
        WorkflowID wfID1 = new WorkflowID("wf1");
        Schedule sch1 = new HourlySchedule();
        SchedulingStrategy str1 = new TrivialSchedulingStrategy();
        Trigger tr1 = new AlwaysTrigger();
        MockExternalService srv1 = new MockExternalService(new MockExternalService.MockExternalStatusRunning());
        Workflow wf1 = new Workflow(wfID1, sch1, str1, tr1, srv1);
        
        Set<Workflow> workflows = new HashSet<Workflow>();
        workflows.add(wf1);
        WorkflowConfiguration cfg = new WorkflowConfiguration(workflows);
        
        MemoryStateDatabase db = new MemoryStateDatabase();

        int slidingWindowHours = 24;
        DateTime current = DateTime.parse("2013-11-27T15:01Z");
        DateTime currentFullHour = Util.toFullHour(current);

        for (int i = 0; i < slidingWindowHours; i++) {
            SlotID id = new SlotID(wfID1, new ScheduledTime(currentFullHour.minusHours(i)));
            SlotState state = new SlotState(id, SlotState.Status.READY);
            db.putSlotState(state);
        }
        
        Scheduler sched = new Scheduler(slidingWindowHours);
        sched.step(new ScheduledTime(current), cfg, db);
        
        Assert.assertEquals(slidingWindowHours, db.size());
        Assert.assertEquals(slidingWindowHours, srv1.getTimes2ExternalID().size());
        
        for (int i = 0; i < slidingWindowHours; i++) {
            ScheduledTime scheduledTime = new ScheduledTime(currentFullHour.minusHours(i));
            SlotID id = new SlotID(wfID1, scheduledTime);
            SlotState state = db.getSlotState(id);
            if (state == null) {
                throw new AssertionError("Slot " + id + " not found.");
            }
            Assert.assertEquals(SlotState.Status.RUNNING, state.getStatus());
            String externalID = state.getExternalID();
            Assert.assertNotNull(externalID);
            Assert.assertEquals(externalID, srv1.getTimes2ExternalID().get(scheduledTime));
        }
    }
    
    /**
     * Use a serial scheduling strategy.
     * 
     * Create one waiting and two ready slots.
     * 
     * Make sure that after a step:
     * 
     * - the waiting slot is ready
     * 
     * - the first ready slot is running
     * 
     * - the second ready slot is still ready
     * 
     * - the running slot's external ID matches the one handed out by the mock external service
     */
    @Test
    public void multipleSlotsTest() throws Exception {
        WorkflowID wfID1 = new WorkflowID("wf1");
        Schedule sch1 = new HourlySchedule();
        SchedulingStrategy str1 = new SerialSchedulingStrategy();
        Trigger tr1 = new AlwaysTrigger();
        MockExternalService srv1 = new MockExternalService(new MockExternalService.MockExternalStatusRunning());
        Workflow wf1 = new Workflow(wfID1, sch1, str1, tr1, srv1);
        
        Set<Workflow> workflows = new HashSet<Workflow>();
        workflows.add(wf1);
        WorkflowConfiguration cfg = new WorkflowConfiguration(workflows);
        
        MemoryStateDatabase db = new MemoryStateDatabase();

        SlotID id1 = new SlotID(wfID1, new ScheduledTime("2013-11-27T20:00Z"));
        SlotID id2 = new SlotID(wfID1, new ScheduledTime("2013-11-27T21:00Z"));
        SlotID id3 = new SlotID(wfID1, new ScheduledTime("2013-11-27T22:00Z"));
        
        SlotState slot1 = new SlotState(id1, SlotState.Status.WAITING);
        SlotState slot2 = new SlotState(id2, SlotState.Status.WAITING).transitionToReady();
        SlotState slot3 = new SlotState(id3, SlotState.Status.WAITING).transitionToReady();
        
        db.putSlotState(slot1);
        db.putSlotState(slot2);
        db.putSlotState(slot3);
        
        int slidingWindowHours = 3;
        DateTime current = DateTime.parse("2013-11-27T22:01Z");

        Scheduler sched = new Scheduler(slidingWindowHours);
        sched.step(new ScheduledTime(current), cfg, db);
        
        SlotState slot1After = db.getSlotState(id1);
        Assert.assertEquals(SlotState.Status.READY, slot1After.getStatus());
        
        SlotState slot2After = db.getSlotState(id2);
        Assert.assertEquals(SlotState.Status.RUNNING, slot2After.getStatus());

        SlotState slot3After = db.getSlotState(id3);
        Assert.assertEquals(SlotState.Status.READY, slot3After.getStatus());
        
        String externalID = slot2After.getExternalID();
        Assert.assertNotNull(externalID);
        Assert.assertEquals(externalID, srv1.getTimes2ExternalID().get(slot2.getScheduledTime()));
    }
    
}
