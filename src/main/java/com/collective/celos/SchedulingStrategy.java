package com.collective.celos;

import java.util.List;

/**
 * A scheduling strategy determines the order in which slots of a workflow are executed.
 *
 * Scheduling strategies are configured on a per-workflow basis.
 * 
 * On each scheduler cycle, a workflow's scheduling strategy is asked
 * which workflow instances (slots) should be executed.
 * 
 * As input for making this decision, the scheduling strategy receives
 * all slot states of the given workflow within the sliding window.  
 * 
 * There's no guarantee that all slots returned by a strategy will be executed.
 */
public interface SchedulingStrategy {

    /**
     * Returns the slots that this strategy wishes to execute.
     * 
     * Only slots in READY state should be returned.  Others will be ignored.
     * 
     * @param states The states of all slots within sliding window for this workflow, with oldest ones first.
     * @return List of slots this strategy wishes to execute - the first in the list will be executed first.
     */
    public List<SlotState> getSchedulingCandidates(List<SlotState> states);

}
