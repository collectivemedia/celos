package com.collective.celos;

import java.util.SortedSet;

/**
 * Determines the times at which a workflow is supposed to run.
 */
public interface Schedule {

    /**
     * Returns the set of scheduled times of this schedule within the
     * window defined by start (inclusive) and end (exclusive).
     * 
     * The scheduler parameter is currently only used by DependentSchedule, so tests
     * of other schedules pass null as scheduler, which is a bit of a hack, but works.
     */
    public SortedSet<ScheduledTime> getScheduledTimes(Scheduler scheduler, ScheduledTime start, ScheduledTime end);

}
