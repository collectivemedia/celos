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
package com.collective.celos.ui;

import com.collective.celos.Util;
import com.collective.celos.WorkflowID;
import com.collective.celos.WorkflowStatus;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

/**
 * All data required by the UI for rendering.
 */
public class UIConfiguration {

    private final ZonedDateTime start;
    private final ZonedDateTime end;
    private final NavigableSet<ZonedDateTime> tileTimes;
    private final List<WorkflowGroup> groups;
    private final Map<WorkflowID, WorkflowStatus> statuses;
    private final URL hueURL; // may be null
    
    public UIConfiguration(ZonedDateTime start, ZonedDateTime end, NavigableSet<ZonedDateTime> tileTimes, List<WorkflowGroup> groups, Map<WorkflowID, WorkflowStatus> statuses, URL hueURL) {
        this.start = Util.requireNonNull(start);
        this.end = Util.requireNonNull(end);
        this.tileTimes = Util.requireNonNull(tileTimes);
        this.groups = Util.requireNonNull(groups);
        this.statuses = Util.requireNonNull(statuses);
        this.hueURL = hueURL;
    }
    
    public ZonedDateTime getStart() {
        return start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public NavigableSet<ZonedDateTime> getTileTimes() {
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
