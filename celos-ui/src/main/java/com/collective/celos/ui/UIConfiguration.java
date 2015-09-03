package com.collective.celos.ui;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Util;
import com.collective.celos.WorkflowID;
import com.collective.celos.WorkflowStatus;

public class UIConfiguration {

    private final ScheduledTime start;
    private final ScheduledTime end;
    private final NavigableSet<ScheduledTime> tileTimes;
    private final List<WorkflowGroup> groups;
    private final Map<WorkflowID, WorkflowStatus> statuses;
    private final URL hueURL; // may be null
    
    public UIConfiguration(ScheduledTime start, ScheduledTime end, NavigableSet<ScheduledTime> tileTimes, List<WorkflowGroup> groups, Map<WorkflowID, WorkflowStatus> statuses, URL hueURL) {
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
