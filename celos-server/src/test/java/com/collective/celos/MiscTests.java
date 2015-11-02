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

import com.collective.celos.trigger.AlwaysTrigger;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

import static org.mockito.Mockito.mock;

public class MiscTests {

    // EQUALS
    
    @Test
    public void scheduledTimeEqualsWorks() {
        Assert.assertEquals(new ScheduledTime("2013-11-18T20:00Z"), new ScheduledTime("2013-11-18T20:00Z"));
        Assert.assertNotSame(new ScheduledTime("2014-11-18T20:00Z"), new ScheduledTime("2013-11-18T20:00Z"));
    }
    
    @Test
    public void slotIDEqualsWorks() {    
        Assert.assertEquals(new SlotID(new WorkflowID("foo"), new ScheduledTime("2013-11-18T20:00Z")),
                            new SlotID(new WorkflowID("foo"), new ScheduledTime("2013-11-18T20:00Z")));
        Assert.assertNotSame(new SlotID(new WorkflowID("bar"), new ScheduledTime("2013-11-18T20:00Z")),
                             new SlotID(new WorkflowID("foo"), new ScheduledTime("2013-11-18T20:00Z")));
        Assert.assertNotSame(new SlotID(new WorkflowID("foo"), new ScheduledTime("2014-11-18T20:00Z")),
                             new SlotID(new WorkflowID("foo"), new ScheduledTime("2013-11-18T20:00Z")));
    }        
        
    @Test
    public void slotStateEqualsWorks() {
        SlotID slotID1 = new SlotID(new WorkflowID("foo"), new ScheduledTime("2013-11-18T20:00Z"));
        SlotID slotID2= new SlotID(new WorkflowID("bar"), new ScheduledTime("2013-11-18T20:00Z"));

        Assert.assertEquals(new SlotState(slotID1, SlotState.Status.READY),
                            new SlotState(slotID1, SlotState.Status.READY));
        Assert.assertNotSame(new SlotState(slotID1, SlotState.Status.READY),
                             new SlotState(slotID1, SlotState.Status.WAITING));
        Assert.assertNotSame(new SlotState(slotID1, SlotState.Status.READY),
                             new SlotState(slotID2, SlotState.Status.READY));   
    }
    
    @Test
    public void workflowIDEqualsWorks() {
        Assert.assertEquals(new WorkflowID("foo"), new WorkflowID("foo"));
        Assert.assertNotSame(new WorkflowID("bar"), new WorkflowID("foo"));
    }
    
    // HASHCODE
    
    /* Tests that objects of a type work correctly as HashMap keys. */
    private <T> void hashCodeTest(T a, T b) {
        HashMap<T, String> map = new HashMap<T, String>();
        map.put(a, "foo");
        map.put(b, "bar");
        Assert.assertEquals(map.get(a), "foo");
        Assert.assertEquals(map.get(b), "bar");
    }
    
    @Test
    public void scheduledTimeHashCodeWorks() {
        hashCodeTest(new ScheduledTime("2013-11-18T20:00Z"), new ScheduledTime("2013-11-19T20:00Z"));
    }
    
    @Test
    public void slotIDHashCodeWorks() {
        hashCodeTest(new SlotID(new WorkflowID("foo"), new ScheduledTime("2013-11-18T20:00Z")),
                     new SlotID(new WorkflowID("bar"), new ScheduledTime("2013-11-18T20:00Z")));
    }
    
    @Test
    public void slotStateHashCodeWorks() {
        SlotID slotID = new SlotID(new WorkflowID("foo"), new ScheduledTime("2013-11-18T20:00Z"));
        hashCodeTest(new SlotState(slotID, SlotState.Status.READY),
                     new SlotState(slotID, SlotState.Status.WAITING));
    }
    
    @Test
    public void workflowIDHashCodeWorks() {
        hashCodeTest(new WorkflowID("foo"), new WorkflowID("bar"));
    }
    
    // OTHER
    
    @Test(expected=IllegalArgumentException.class)
    public void cannotCreateWorkflowWithWhitespaceID() {
        new WorkflowID("   \n \t");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void cannotCreateWorkflowWithIDContainingSlash() {
        new WorkflowID("workflow/1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void scheduledTimeMustBeUTC() {
        new ScheduledTime("2013-11-12T20:00");
    }
    
    @Test
    public void tooTrivialButStill_alwaysTriggerAlwaysTriggers() throws Exception {
        StateDatabaseConnection connection = mock(StateDatabaseConnection.class);
        Assert.assertTrue(new AlwaysTrigger().isDataAvailable(connection, ScheduledTime.now(), new ScheduledTime("2013-11-21T20:00Z")));
    }
        
}
