package com.collective.celos;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.Predicate;

import com.collective.celos.SlotState.Status;

/*
 * Predicate that compares the status of a SlotState object to a set of target
 * values.
 * 
 * Instances of this predicate are for use with CollectionUtils methods.
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
