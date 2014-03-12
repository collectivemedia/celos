package com.collective.celos.exposed;

import java.util.SortedSet;

/**
 * Determines the times at which a workflow is supposed to run.
 */
public interface Schedule {

    /**
     * Returns the set of scheduled times of this schedule within the
     * window defined by start (inclusive) and end (exclusive).
     */
    public SortedSet<ScheduledTime> getScheduledTimes(ScheduledTime start, ScheduledTime end);

}
