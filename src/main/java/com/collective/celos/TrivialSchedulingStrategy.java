package com.collective.celos;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Trivial scheduling strategy that submits as many ready jobs as possible in parallel. 
 */
public class TrivialSchedulingStrategy implements SchedulingStrategy {

    TrivialSchedulingStrategy() {
    }

    public TrivialSchedulingStrategy(Properties ignored) {
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
