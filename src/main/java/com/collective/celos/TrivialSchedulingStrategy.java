package com.collective.celos;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Trivial scheduling strategy that submits as many ready jobs as possible in parallel. 
 */
public class TrivialSchedulingStrategy implements SchedulingStrategy {

    public List<SlotID> getSchedulingCandidates(List<SlotState> states) {
        List<SlotID> slots = new LinkedList<SlotID>();
        for (Iterator<SlotState> it = states.iterator(); it.hasNext();) {
            SlotState slotState = it.next();
            if (slotState.getStatus() == SlotState.Status.READY) {
                slots.add(slotState.getSlotID());
            }
        }
        return slots;
    }

}
