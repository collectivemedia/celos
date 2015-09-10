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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RerunState extends ValueObject {

    public static final int EXPIRATION_DAYS = 14;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String RERUN_TIME_PROP = "scheduledTime";

    // The wallclock time at which the slot was marked for rerun
    private final ScheduledTime rerunTime;
    
    public RerunState(ScheduledTime rerunTime) {
        this.rerunTime = Util.requireNonNull(rerunTime);
    }

    public ScheduledTime getRerunTime() {
        return rerunTime;
    }

    public boolean isExpired(ScheduledTime now) {
        return rerunTime.plusDays(EXPIRATION_DAYS).getDateTime().isBefore(now.getDateTime());
    }
    
    public ObjectNode toJSONNode() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put(RERUN_TIME_PROP, rerunTime.toString());
        return node;
    }
    
    public static RerunState fromJSONNode(ObjectNode node) {
        String timeStr = node.get(RERUN_TIME_PROP).textValue();
        return new RerunState(new ScheduledTime(timeStr));
    }
    
}
