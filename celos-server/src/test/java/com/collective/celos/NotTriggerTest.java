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

import com.collective.celos.trigger.NotTrigger;
import junit.framework.Assert;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class NotTriggerTest {
    
    @Test
    public void invertsTrue() throws Exception {
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertFalse(new NotTrigger(AndTriggerTest.createAlwaysTrigger()).isDataAvailable(scheduler, ScheduledTime.now(), ScheduledTime.now()));
    }
    
    @Test
    public void invertsFalse() throws Exception {
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertTrue(new NotTrigger(AndTriggerTest.createNeverTrigger()).isDataAvailable(scheduler, ScheduledTime.now(), ScheduledTime.now()));
    }
    
}
