package com.collective.celos;

import java.util.LinkedList;
import java.util.List;

/**
 * Trivial scheduling strategy that submits as many ready jobs as possible in parallel. 
 */
public class TrivialSchedulingStrategy implements SchedulingStrategy {

    public TrivialSchedulingStrategy() {
    }
    
    public List<SlotState> getSchedulingCandidates(List<SlotState> states) {
        List<SlotState> candidates = new LinkedList<SlotState>();
        for (SlotState slotState : states) {
            if (slotState.getStatus() == SlotState.Status.READY) {
                candidates.add(slotState);
            }
        }
        return candidates;
    }

}
