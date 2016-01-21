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

import com.collective.celos.Util;

import java.util.List;

/**
 * Human-readable information about the status of a trigger.
 */
public final class TriggerStatus {

    private final String type;
    private final boolean ready;
    private final String description;
    private final List<TriggerStatus> subStatuses;

    public TriggerStatus(String triggerClassName, boolean ready, String description, List<TriggerStatus> subStatuses) {
        this.type = Util.requireNonNull(triggerClassName);
        this.ready = ready;
        this.description = Util.requireNonNull(description);
        this.subStatuses = Util.requireNonNull(subStatuses);
    }

    public String getType() {
        return type;
    }

    public boolean isReady() {
        return ready;
    }

    public String getDescription() {
        return description;
    }

    public List<TriggerStatus> getSubStatuses() {
        return subStatuses;
    }

}
