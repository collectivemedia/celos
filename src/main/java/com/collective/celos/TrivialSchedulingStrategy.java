package com.collective.celos;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Trivial scheduling strategy that submits as many ready jobs as possible in parallel. 
 */
public class TrivialSchedulingStrategy implements SchedulingStrategy {

    public TrivialSchedulingStrategy(ObjectNode ignored) {
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
