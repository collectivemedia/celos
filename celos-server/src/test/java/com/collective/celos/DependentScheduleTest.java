/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

import java.util.Collections;
import java.util.SortedSet;

import com.collective.celos.trigger.AlwaysTrigger;
import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.WorkflowInfo.ContactsInfo;
import com.google.common.collect.Sets;

public class DependentScheduleTest {

    @Test
    public void throwsExceptionContainingWorkflowIDIfWorkflowNotFound() {
        WorkflowConfiguration emptyConf = new WorkflowConfiguration();
        int slidingWindowHours = 24;
        Scheduler scheduler = new Scheduler(emptyConf, slidingWindowHours);
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
        int slidingWindowHours = 24;
        Scheduler scheduler = new Scheduler(conf, slidingWindowHours);
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
                        Workflow.DEFAULT_WAIT_TIMEOUT_SECONDS,
                        new WorkflowInfo(null, Collections.<ContactsInfo>emptyList())));
        
        DependentSchedule schedule = new DependentSchedule(new WorkflowID(id));
        SortedSet<ScheduledTime> times = schedule.getScheduledTimes(scheduler, schedulingStartTime, schedulingEndTime);
        Assert.assertEquals(Sets.newHashSet(new ScheduledTime("2015-02-23T10:00Z"), new ScheduledTime("2015-02-23T11:00Z"), new ScheduledTime("2015-02-23T12:00Z")),
                            times);
        
    }

}
