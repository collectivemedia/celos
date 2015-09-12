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
