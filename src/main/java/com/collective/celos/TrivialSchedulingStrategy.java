package com.collective.celos;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Trivial scheduling strategy that submits as many ready jobs as possible in parallel. 
 */
public class TrivialSchedulingStrategy implements SchedulingStrategy {

    public List<SlotState> getSchedulingCandidates(List<SlotState> states) {
        List<SlotState> slots = new LinkedList<SlotState>();
        for (Iterator<SlotState> it = states.iterator(); it.hasNext();) {
            SlotState slotState = it.next();
            if (slotState.getStatus() == SlotState.Status.READY) {
                slots.add(slotState);
            }
        }
        return slots;
    }

}
