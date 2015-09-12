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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RerunStateTest {

    @Test
    public void testExpiration() {
        RerunState st = new RerunState(new ScheduledTime("2015-09-07T00:00Z"));
        Assert.assertTrue(st.isExpired(new ScheduledTime("2015-10-07T00:00Z")));
        Assert.assertFalse(st.isExpired(new ScheduledTime("2015-09-14T00:00Z")));
        Assert.assertFalse(st.isExpired(new ScheduledTime("2014-09-14T00:00Z")));
    }
    
    @Test
    public void testJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        RerunState st = new RerunState(new ScheduledTime("2015-09-07T00:00Z"));
        String json = mapper.writeValueAsString(st.toJSONNode());
        Assert.assertEquals("{\"rerunTime\":\"2015-09-07T00:00:00.000Z\"}", json);
        Assert.assertEquals(st, RerunState.fromJSONNode((ObjectNode) mapper.readTree(json)));
    }
    
}
