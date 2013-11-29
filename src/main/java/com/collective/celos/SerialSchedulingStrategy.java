package com.collective.celos;

import static com.collective.celos.SlotState.Status.READY;
import static com.collective.celos.SlotState.Status.RUNNING;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/*
 * This SchedulingStrategy implementation will be used for Pythia. It runs jobs
 * serially, and only ever runs a single job at a time.
 * 
 * - If there is a RUNNING slot, return an empty list (because a slot is already running).
 * - Otherwise, find slots that are READY.
 * - If any exist, return the first one, in a list.
 * - Otherwise, return an empty list.
 */
public class SerialSchedulingStrategy implements SchedulingStrategy {

    SerialSchedulingStrategy() {
    }

    public SerialSchedulingStrategy(Properties ignored) {
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

        List<SlotState> result = new LinkedList<SlotState>();

        if (CollectionUtils.exists(states, RUNNING_PREDICATE)) {
            return result;
        }

        SlotState first = (SlotState) CollectionUtils.find(states, CANDIDATE_PREDICATE);
        if (first != null) {
            result.add(first);
        }

        return result;
    }

}
