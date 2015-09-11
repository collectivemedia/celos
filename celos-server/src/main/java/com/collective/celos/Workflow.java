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

import com.collective.celos.trigger.Trigger;

import java.time.ZonedDateTime;

/**
 * A periodical task.
 */
public class Workflow {

    public static final ZonedDateTime DEFAULT_START_TIME = ZonedDateTime.parse("1970-01-01T00:00Z");
    public static final int DEFAULT_WAIT_TIMEOUT_SECONDS = Integer.MAX_VALUE;
    
    private final WorkflowID id;
    private final Schedule schedule;
    private final SchedulingStrategy schedulingStrategy;
    private final Trigger trigger;
    private final ExternalService externalService;
    private final int maxRetryCount;
    private final ZonedDateTime startTime;
    private final int waitTimeoutSeconds;
    private final WorkflowInfo workflowInfo;
    
    public Workflow(WorkflowID id,
                    Schedule schedule,
                    SchedulingStrategy strategy,
                    Trigger trigger,
                    ExternalService service,
                    int maxRetryCount,
                    ZonedDateTime startTime,
                    int waitTimeoutSeconds,
                    WorkflowInfo workflowInfo) {
        this.id = Util.requireNonNull(id);
        this.schedule = Util.requireNonNull(schedule);
        this.schedulingStrategy = Util.requireNonNull(strategy);
        this.trigger = Util.requireNonNull(trigger);
        this.externalService = Util.requireNonNull(service);
        this.maxRetryCount = maxRetryCount;
        this.startTime = Util.requireNonNull(startTime);
        this.workflowInfo = Util.requireNonNull(workflowInfo);
        this.waitTimeoutSeconds = waitTimeoutSeconds;
    }

    public WorkflowID getID() {
        return id;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public SchedulingStrategy getSchedulingStrategy() {
        return schedulingStrategy;
    }

    public Trigger getTrigger() {
        return trigger;
    }
    
    public ExternalService getExternalService() {
        return externalService;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public WorkflowInfo getWorkflowInfo() {
        return workflowInfo;
    }

    public int getWaitTimeoutSeconds() {
        return waitTimeoutSeconds;
    }
}
