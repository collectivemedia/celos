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
package com.collective.celos.old;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Util;
import com.collective.celos.WorkflowID;
import com.collective.celos.WorkflowStatus;
import com.collective.celos.old.WorkflowGroup;

/**
 * All data required by the UI for rendering.
 */
public class Configuration {

    private final ScheduledTime start;
    private final ScheduledTime end;
    private final NavigableSet<ScheduledTime> tileTimes;
    private final List<WorkflowGroup> groups;
    private final Map<WorkflowID, WorkflowStatus> statuses;
    private final URL hueURL; // may be null
    
    public Configuration(ScheduledTime start, ScheduledTime end, NavigableSet<ScheduledTime> tileTimes, List<WorkflowGroup> groups, Map<WorkflowID, WorkflowStatus> statuses, URL hueURL) {
        this.start = Util.requireNonNull(start);
        this.end = Util.requireNonNull(end);
        this.tileTimes = Util.requireNonNull(tileTimes);
        this.groups = Util.requireNonNull(groups);
        this.statuses = Util.requireNonNull(statuses);
        this.hueURL = hueURL;
    }
    
    public ScheduledTime getStart() {
        return start;
    }

    public ScheduledTime getEnd() {
        return end;
    }

    public NavigableSet<ScheduledTime> getTileTimes() {
        return tileTimes;
    }

    public List<WorkflowGroup> getGroups() {
        return groups;
    }

    public Map<WorkflowID, WorkflowStatus> getStatuses() {
        return statuses;
    }

    public URL getHueURL() {
        return hueURL;
    }

}
