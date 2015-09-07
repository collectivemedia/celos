package com.collective.celos;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.Predicate;

import com.collective.celos.SlotState.Status;

/**
 * Ivor who's a Scala wonk did this.
 */
public class SlotStateStatusPredicate implements Predicate {
    
    private Set<Status> targetValues;

    public SlotStateStatusPredicate(Status... targetValues) {
        this.targetValues = new HashSet<Status>(Arrays.asList(targetValues));
        if (this.targetValues.isEmpty()) {
            throw new IllegalArgumentException("please specify some status values");
        }
    }

    @Override
    public boolean evaluate(Object object) {
        SlotState state = (SlotState) object;
        return targetValues.contains(state.status);
    }

}
