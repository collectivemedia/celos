package com.collective.celos;

import static com.collective.celos.SlotState.Status.READY;
import static com.collective.celos.SlotState.Status.RUNNING;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
	public List<SlotID> getSchedulingCandidates(List<SlotState> states) {

		List<SlotID> result = new LinkedList<SlotID>();

		if (CollectionUtils.exists(states, RUNNING_PREDICATE)) {
			return result;
		}

		// These generics drive me nuts. Can someone fix this the right way?
		@SuppressWarnings("unchecked")
		Collection<SlotState> readyStates = CollectionUtils.select(states,
				CANDIDATE_PREDICATE);

		if (!readyStates.isEmpty()) {
			SlotState first = readyStates.iterator().next();
			result.add(first.getSlotID());
		}

		return result;
	}
	
	/*
	 * I couldn't resist: here's a Scala implementation of the same method.
	 * 
	 *  def getSchedulingCandidates(
     *    states: List[SlotState]): List[SlotID] = {
     *   
     *   if (states.exists(_.status == RUNNING)) {
     *     List()
     *   } else {
	 *     states
	 *       .filter(_.status == READY)
	 *       .take(1)
	 *       .map(_.slotID)
     *     }
     *   }
	 */

}
