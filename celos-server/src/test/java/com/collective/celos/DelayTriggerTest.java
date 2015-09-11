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

import com.collective.celos.trigger.DelayTrigger;
import com.collective.celos.trigger.Trigger;
import com.collective.celos.trigger.TriggerStatus;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.mockito.Mockito.mock;

public class DelayTriggerTest {

    private Scheduler scheduler = mock(Scheduler.class);
    private ZonedDateTime now = ZonedDateTime.parse("2014-01-01T05:00:00Z");

    @Test
    public void canBeConfiguredFromJSON() throws Exception {
        Assert.assertEquals(12, new DelayTrigger(12).getSeconds());
    }
    
    @Test
    public void works() throws Exception {
        Trigger t = new DelayTrigger(60 * 60);
        Assert.assertTrue(t.isDataAvailable(scheduler, now, ZonedDateTime.parse("1980-01-01T03:59:00Z")));
        Assert.assertTrue(t.isDataAvailable(scheduler, now, ZonedDateTime.parse("2014-01-01T03:59:00Z")));
        Assert.assertTrue(t.isDataAvailable(scheduler, now, ZonedDateTime.parse("2014-01-01T03:59:59Z")));
        Assert.assertFalse(t.isDataAvailable(scheduler, now, ZonedDateTime.parse("2014-01-01T04:00:00Z")));
        Assert.assertFalse(t.isDataAvailable(scheduler, now, ZonedDateTime.parse("2014-01-01T05:00:00Z")));
        Assert.assertFalse(t.isDataAvailable(scheduler, now, ZonedDateTime.parse("2014-01-01T06:00:00Z")));
        Assert.assertFalse(t.isDataAvailable(scheduler, now, ZonedDateTime.parse("2080-01-01T06:00:00Z")));
    }


    @Test
    public void descriptionReady() throws Exception {
        Trigger t = new DelayTrigger(60 * 60);
        final TriggerStatus triggerStatus = t.getTriggerStatus(scheduler, now.plusHours(2), now);
        final String description = triggerStatus.getDescription();
        Assert.assertTrue(triggerStatus.isReady());
        Assert.assertEquals("Ready since 2014-01-01T06:00:00.000Z", description);
    }

    @Test
    public void descriptionNotReady() throws Exception {
        Trigger t = new DelayTrigger(60 * 60);
        final TriggerStatus triggerStatus = t.getTriggerStatus(scheduler, now, now);
        final String description = triggerStatus.getDescription();
        Assert.assertFalse(triggerStatus.isReady());
        Assert.assertEquals("Delayed until 2014-01-01T06:00:00.000Z", description);
    }

}
