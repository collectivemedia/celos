package com.collective.celos;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

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
    
    @Test
    public void updatesWaitingSlotsToReady() throws Exception {
        WorkflowID wfID1 = new WorkflowID("wf1");
        Schedule sch1 = new HourlySchedule();
        SchedulingStrategy str1 = new SerialSchedulingStrategy();
        Trigger tr1 = new AlwaysTrigger();
        ExternalService srv1 = new MockExternalService();
        Workflow wf1 = new Workflow(wfID1, sch1, str1, tr1, srv1);
        
        Set<Workflow> workflows = new HashSet<Workflow>();
        workflows.add(wf1);
        WorkflowConfiguration cfg = new WorkflowConfiguration(workflows);
        
        StateDatabase db = new MemoryStateDatabase();

        int slidingWindowHours = 24;
        DateTime current = DateTime.parse("2013-11-27T15:01Z");
        DateTime currentFullHour = Util.toFullHour(current);
        
        Scheduler sched = new Scheduler(slidingWindowHours);
        sched.step(new ScheduledTime(current), cfg, db);
        
        for (int i = 0; i < slidingWindowHours; i++) {
            SlotID id = new SlotID(wfID1, new ScheduledTime(currentFullHour.minusHours(i)));
            SlotState state = db.getSlotState(id);
            if (state == null) {
                throw new AssertionError("Slot " + id + " not found.");
            }
            Assert.assertEquals(SlotState.Status.READY, state.getStatus());
        }
        
    }
    
}
