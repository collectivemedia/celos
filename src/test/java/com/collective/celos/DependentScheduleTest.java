package com.collective.celos;

import java.util.Collections;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.WorkflowInfo.ContactsInfo;
import com.google.common.collect.Sets;

public class DependentScheduleTest {

    @Test
    public void throwsExceptionContainingWorkflowIDIfWorkflowNotFound() {
        WorkflowConfiguration emptyConf = new WorkflowConfiguration();
        StateDatabase emptyDB = new MemoryStateDatabase();
        int slidingWindowHours = 24;
        Scheduler scheduler = new Scheduler(emptyConf, emptyDB, slidingWindowHours);
        String id = "nonexisting-workflow";
        DependentSchedule schedule = new DependentSchedule(new WorkflowID(id));
        try {
            schedule.getScheduledTimes(scheduler, ScheduledTime.now(), ScheduledTime.now());
        } catch(Exception e) {
            if (!e.getMessage().contains(id)) {
                Assert.assertFalse(true);
            }
        }
    }

    @Test
    public void usesScheduleOfOtherWorkflow() {
        
        ScheduledTime workflowStartTime = new ScheduledTime("2015-02-23T00:00Z");
        ScheduledTime schedulingStartTime = new ScheduledTime("2015-02-23T10:00Z");
        ScheduledTime schedulingEndTime = new ScheduledTime("2015-02-23T12:01Z");

        WorkflowConfiguration conf = new WorkflowConfiguration();
        StateDatabase emptyDB = new MemoryStateDatabase();
        int slidingWindowHours = 24;
        Scheduler scheduler = new Scheduler(conf, emptyDB, slidingWindowHours);
        String id = "other-workflow";
        Schedule otherSchedule = new HourlySchedule();
        
        conf.addWorkflow(
                new Workflow(
                        new WorkflowID(id), 
                        otherSchedule,
                        new SerialSchedulingStrategy(1),
                        new AlwaysTrigger(),
                        new MockExternalService(
                                new MockExternalService.MockExternalStatusSuccess()),
                        slidingWindowHours,
                        workflowStartTime,
                        new WorkflowInfo(null, Collections.<ContactsInfo>emptyList())));
        
        DependentSchedule schedule = new DependentSchedule(new WorkflowID(id));
        SortedSet<ScheduledTime> times = schedule.getScheduledTimes(scheduler, schedulingStartTime, schedulingEndTime);
        Assert.assertEquals(Sets.newHashSet(new ScheduledTime("2015-02-23T10:00Z"), new ScheduledTime("2015-02-23T11:00Z"), new ScheduledTime("2015-02-23T12:00Z")),
                            times);
        
    }

}
