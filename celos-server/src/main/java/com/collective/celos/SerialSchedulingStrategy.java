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

import static com.collective.celos.SlotState.Status.READY;
import static com.collective.celos.SlotState.Status.RUNNING;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.google.common.collect.Lists;

/**
 * Submits workflow slots oldest first with a configurable concurrency level.
 */
public class SerialSchedulingStrategy implements SchedulingStrategy {

    private int concurrencyLevel;

    public SerialSchedulingStrategy(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
    }
    
    /*
     * Predicate for finding RUNNING slots.
     */
    private final Predicate RUNNING_PREDICATE
            = new SlotStateStatusPredicate(RUNNING);

    /*
     * Candidates for submission are slots that are READY. We may want to
     * include WAITING jobs in the future, too.
     */
    private final Predicate CANDIDATE_PREDICATE
            = new SlotStateStatusPredicate(READY /* , WAITING */);

    @Override
    public List<SlotState> getSchedulingCandidates(List<SlotState> states) {

        int slotsRunning = CollectionUtils.countMatches(states, RUNNING_PREDICATE);
        if (slotsRunning >= concurrencyLevel) {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        Collection<SlotState> candidates = CollectionUtils.select(states, CANDIDATE_PREDICATE);

        if (!candidates.isEmpty()) {
            int elemsToGet = Math.min(candidates.size(), concurrencyLevel - slotsRunning);
            return Lists.newArrayList(candidates).subList(0, elemsToGet);
        }

        return Collections.emptyList();
    }

    public int getConcurrencyLevel() {
        return concurrencyLevel;
    }
}
