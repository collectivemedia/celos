/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
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
        SlotID id = new SlotID(new WorkflowID("x"), new ScheduledTime(
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
        SlotID id = new SlotID(new WorkflowID("x"), new ScheduledTime(
                "2013-11-18T20:00Z"));

        assertTrue(predicate.evaluate(new SlotState(id, WAITING)));
        assertTrue(predicate.evaluate(new SlotState(id, FAILURE)));

        assertFalse(predicate.evaluate(new SlotState(id, READY)));
        assertFalse(predicate.evaluate(new SlotState(id, RUNNING)));
        assertFalse(predicate.evaluate(new SlotState(id, SUCCESS)));
    }

}
