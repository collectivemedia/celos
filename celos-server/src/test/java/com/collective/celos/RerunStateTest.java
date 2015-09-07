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
