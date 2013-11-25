package com.collective.celos;

import java.util.Set;

/**
 * Determines the times at which a workflow is supposed to run.
 */
public interface Schedule {

    /**
     * Returns the set of scheduled times of this schedule within the
     * window defined by start (inclusive) and end (exclusive).
     */
    public Set<ScheduledTime> getScheduledTimes(ScheduledTime start, ScheduledTime end);

}
