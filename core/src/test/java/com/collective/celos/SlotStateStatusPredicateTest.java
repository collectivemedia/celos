package com.collective.celos;

import static com.collective.celos.SlotState.Status.FAILURE;
import static com.collective.celos.SlotState.Status.READY;
import static com.collective.celos.SlotState.Status.RUNNING;
import static com.collective.celos.SlotState.Status.SUCCESS;
import static com.collective.celos.SlotState.Status.WAITING;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SlotStateStatusPredicateTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNoValues() {
        new SlotStateStatusPredicate();
    }

    @Test
    public void testSingleValue() {
        SlotStateStatusPredicate predicate = new SlotStateStatusPredicate(READY);
        SlotID id = new SlotID(new WorkflowID("x"), new ScheduledTimeImpl(
                "2013-11-18T20:00Z"));

        assertTrue(predicate.evaluate(new SlotState(id, READY)));

        assertFalse(predicate.evaluate(new SlotState(id, WAITING)));
        assertFalse(predicate.evaluate(new SlotState(id, RUNNING)));
        assertFalse(predicate.evaluate(new SlotState(id, SUCCESS)));
        assertFalse(predicate.evaluate(new SlotState(id, FAILURE)));
    }

    @Test
    public void testMultipleValues() {
        SlotStateStatusPredicate predicate = new SlotStateStatusPredicate(
                WAITING, FAILURE);
        SlotID id = new SlotID(new WorkflowID("x"), new ScheduledTimeImpl(
                "2013-11-18T20:00Z"));

        assertTrue(predicate.evaluate(new SlotState(id, WAITING)));
        assertTrue(predicate.evaluate(new SlotState(id, FAILURE)));

        assertFalse(predicate.evaluate(new SlotState(id, READY)));
        assertFalse(predicate.evaluate(new SlotState(id, RUNNING)));
        assertFalse(predicate.evaluate(new SlotState(id, SUCCESS)));
    }

}
