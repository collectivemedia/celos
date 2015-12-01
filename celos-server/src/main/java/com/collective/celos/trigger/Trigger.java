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
package com.collective.celos.trigger;

import java.util.Collections;
import java.util.List;

import com.collective.celos.ScheduledTime;
import com.collective.celos.database.StateDatabaseConnection;

/**
 * A trigger is called to determine data availability for a workflow.
 */
public abstract class Trigger {

    /**
     * Returns information about data availablity for the given scheduled time.
     */
    public abstract TriggerStatus getTriggerStatus(StateDatabaseConnection connection, ScheduledTime now, ScheduledTime scheduledTime) throws Exception;

    /**
     * Returns true if data is available for the given scheduled time, false if not.
     * For clients that don't need the full TriggerStatus information.
     */
    public final boolean isDataAvailable(StateDatabaseConnection connection, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        return getTriggerStatus(connection, now, scheduledTime).isReady();
    }

    protected final TriggerStatus makeTriggerStatus(boolean ready, String description, List<TriggerStatus> subStatuses) {
        return new TriggerStatus(this.getClass().getSimpleName(), ready, description, subStatuses);
    }
    
    protected final TriggerStatus makeTriggerStatus(boolean ready, String description) {
        return makeTriggerStatus(ready, description, Collections.emptyList());
    }
    
}
