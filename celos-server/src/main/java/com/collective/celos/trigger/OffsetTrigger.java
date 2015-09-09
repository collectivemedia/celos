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
import com.collective.celos.Util;

import java.util.Collections;

/**
 * Trigger that offsets a nested trigger into the past or future.
 */
public class OffsetTrigger extends Trigger {

    private final int seconds;
    private final Trigger trigger;

    public OffsetTrigger(int seconds, Trigger trigger) {
        this.seconds = seconds;
        this.trigger = Util.requireNonNull(trigger);
    }

    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        TriggerStatus status = trigger.getTriggerStatus(scheduler, now, scheduledTime.plusSeconds(seconds));
        boolean ready = status.isReady();
        return makeTriggerStatus(ready, humanReadableDescription(ready), Collections.singletonList(status));
    }

    private String humanReadableDescription(boolean ready) {
        if (ready) {
            return "Nested trigger offset by " + getSeconds() + " seconds ready";
        } else {
            return  "Nested trigger offset by " + getSeconds() + " seconds not ready";
        }
    }

    public int getSeconds() {
        return seconds;
    }

    public Trigger getTrigger() {
        return trigger;
    }

}
