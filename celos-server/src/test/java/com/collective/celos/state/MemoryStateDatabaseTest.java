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
package com.collective.celos.state;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import junit.framework.Assert;
import org.junit.Test;

public class MemoryStateDatabaseTest {

    @Test
    public void getAndPutWorks() throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        SlotID slotID = new SlotID(new WorkflowID("foo"), new ScheduledTime("2013-11-27T14:50Z"));
        Assert.assertEquals(null, db.getSlotState(slotID));
        SlotState state = new SlotState(slotID, SlotState.Status.READY);
        db.putSlotState(state);
        Assert.assertEquals(state, db.getSlotState(slotID));
        Assert.assertEquals(null, db.getSlotState(new SlotID(new WorkflowID("bar"), new ScheduledTime("2013-11-27T14:50Z"))));
    }
    
}
