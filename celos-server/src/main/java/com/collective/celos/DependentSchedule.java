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

import java.util.SortedSet;

/**
 * Makes a workflow use the same schedule as another workflow.
 */
public class DependentSchedule implements Schedule {

    private final WorkflowID otherWorkflowID;

    public DependentSchedule(WorkflowID otherWorkflowID) {
        this.otherWorkflowID = Util.requireNonNull(otherWorkflowID);
    }
    
    @Override
    public SortedSet<ScheduledTime> getScheduledTimes(Scheduler scheduler, ScheduledTime start, ScheduledTime end) {
        Workflow otherWorkflow = scheduler.getWorkflowConfiguration().findWorkflow(getOtherWorkflowID());
        if (otherWorkflow == null) {
            throw new RuntimeException("Workflow " + getOtherWorkflowID() + " doesn't exist.");
        }
        Schedule otherSchedule = otherWorkflow.getSchedule();
        return otherSchedule.getScheduledTimes(scheduler, start, end);
    }

    public WorkflowID getOtherWorkflowID() {
        return otherWorkflowID;
    }

}
