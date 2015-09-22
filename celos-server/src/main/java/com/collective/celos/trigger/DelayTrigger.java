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

import com.collective.celos.SlotID;
import org.joda.time.DateTime;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;

import java.util.Collections;
import java.util.Set;

/**
 * A trigger that signals data availability for a given scheduled time
 * only if it is a configurable number of seconds past the current time.
 * 
 * In combination with an AND trigger, this allows to delay the firing
 * of another trigger, for example to clean up data after a day.
 * 
 * The following example shows a trigger that only fires if the given
 * HDFS path is available, and the current time is one day after 
 * the workflow's scheduled time.
 * 
 * var oneDay = 60 * 60 * 24;
 * andTrigger(delayTrigger(oneDay), hdfsCheckTrigger("/${year}/${month}/${day}/..."))
*/
public class DelayTrigger extends Trigger {

    private final int seconds;
    
    public DelayTrigger(int seconds) throws Exception {
        this.seconds = seconds;
    }

    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        DateTime nowDT = now.getDateTime();
        DateTime waitUntilDT = scheduledTime.getDateTime().plusSeconds(getSeconds());
        boolean ready = nowDT.isAfter(waitUntilDT);
        return makeTriggerStatus(ready, humanReadableDescription(ready, waitUntilDT));
    }

    @Override
    public Set<SlotID> findSlotsThatDependOnTime(ScheduledTime scheduledTime) {
        return Collections.emptySet();
    }

    @Override
    public Set<ScheduledTime> findTimesThatDependOnSlot(SlotID other) {
        return Collections.emptySet();
    }

    private String humanReadableDescription(boolean ready, DateTime waitUntilDT) {
        if (ready) {
            return "Ready since " + waitUntilDT.toString();
        } else {
            return "Delayed until " + waitUntilDT.toString();
        }
    }

    public int getSeconds() {
        return seconds;
    }

}
