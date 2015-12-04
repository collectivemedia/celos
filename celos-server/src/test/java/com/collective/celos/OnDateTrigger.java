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

import com.collective.celos.database.StateDatabaseConnection;
import com.collective.celos.trigger.Trigger;
import com.collective.celos.trigger.TriggerStatus;

import java.util.Collections;

public class OnDateTrigger extends Trigger {

    private final ScheduledTime scheduledTime;

    public OnDateTrigger(String scheduledTime) {
        this(new ScheduledTime(scheduledTime));
    }

    public OnDateTrigger(ScheduledTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }


    @Override
    public TriggerStatus getTriggerStatus(StateDatabaseConnection connection, ScheduledTime now, ScheduledTime time) throws Exception {
        return makeTriggerStatus(time.equals(scheduledTime), "unused", Collections.<TriggerStatus>emptyList());
    }


}
