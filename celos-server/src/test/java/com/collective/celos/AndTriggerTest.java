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

import java.util.Arrays;
import java.util.Collections;

import com.collective.celos.trigger.AlwaysTrigger;
import com.collective.celos.trigger.AndTrigger;
import com.collective.celos.trigger.Trigger;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class AndTriggerTest {
    
    @Test
    public void returnsTrueWhenAllSubTriggersReturnTrue() throws Exception {
        AndTrigger trigger = new AndTrigger(Arrays.asList(new Trigger[] { createAlwaysTrigger(), createAlwaysTrigger(), createAlwaysTrigger() }));
        StateDatabaseConnection connection = mock(StateDatabaseConnection.class);
        Assert.assertTrue(trigger.isDataAvailable(connection, ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }
    
    @Test
    public void returnsFalseWhenOnlyOneSubTriggerReturnsTrue() throws Exception {
        AndTrigger trigger = new AndTrigger(Arrays.asList(new Trigger[] { createAlwaysTrigger(), createNeverTrigger(), createAlwaysTrigger() }));
        StateDatabaseConnection connection = mock(StateDatabaseConnection.class);
        Assert.assertFalse(trigger.isDataAvailable(connection, ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }

    @Test
    public void returnsFalseWhenNoSubTriggersReturnsTrue() throws Exception {
        AndTrigger trigger = new AndTrigger(Arrays.asList(new Trigger[] { createNeverTrigger(), createNeverTrigger(), createNeverTrigger() }));
        StateDatabaseConnection connection = mock(StateDatabaseConnection.class);
        Assert.assertFalse(trigger.isDataAvailable(connection, ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }
    
    @Test
    public void returnsTrueWhenThereAreNoSubTriggers() throws Exception {
        AndTrigger trigger = new AndTrigger(Collections.<Trigger>emptyList());
        StateDatabaseConnection connection = mock(StateDatabaseConnection.class);
        Assert.assertTrue(trigger.isDataAvailable(connection, ScheduledTime.now(), new ScheduledTime("2014-02-26T16:13:00Z")));
    }
    
    public static Trigger createAlwaysTrigger() {
        return new AlwaysTrigger();
    }
    
    public static Trigger createNeverTrigger() {
        return new NeverTrigger();
    }
    
}
