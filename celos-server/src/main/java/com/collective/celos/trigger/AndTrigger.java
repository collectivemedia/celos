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

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.SlotID;

import java.util.*;

/**
 * Trigger that takes N nested triggers and does a logical AND.
 */
public class AndTrigger extends Trigger {

    private final List<Trigger> triggers = new LinkedList<>();
    
    public AndTrigger(List<Trigger> triggers) throws Exception {
        this.triggers.addAll(triggers);
    }
    
    private boolean checkSubTriggers(List<TriggerStatus> subStatuses) throws Exception {
        for (TriggerStatus status : subStatuses) {
            if (!status.isReady()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        final List<TriggerStatus> subStatuses = new ArrayList<>();
        for (Trigger trigger : triggers) {
            subStatuses.add(trigger.getTriggerStatus(scheduler, now, scheduledTime));
        }
        boolean ready = this.checkSubTriggers(subStatuses);
        return makeTriggerStatus(ready, humanReadableDescription(ready), subStatuses);
    }

    @Override
    public Set<SlotID> findDependentSlots(ScheduledTime scheduledTime) {
        Set<SlotID> idSet = new HashSet<>();
        for (Trigger childTrigger: triggers) {
            idSet.addAll(childTrigger.findDependentSlots(scheduledTime));
        }
        return idSet;
    }

    private String humanReadableDescription(boolean ready) {
        if (ready) {
            return "All nested triggers are ready";
        } else {
            return "Not all nested triggers are ready";
        }
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

}
