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
package com.collective.celos.servlet;

import javax.servlet.http.HttpServletRequest;

import com.collective.celos.ScheduledTime;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class AbstractServletTest {

    @Test
    public void getRequestTimeParameterWorks() {
        @SuppressWarnings("serial")
        AbstractServlet srv = new AbstractServlet() {};
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        String timeString = "2013-12-18T20:00Z";
        Mockito.when(req.getParameter("time")).thenReturn(timeString);
        Assert.assertEquals(new ScheduledTime(timeString), srv.getRequestTime(req));
    }
    
    @Test
    public void getRequestTimeUsesCurrentTimeWhenNotSupplied() {
        @SuppressWarnings("serial")
        AbstractServlet srv = new AbstractServlet() {};
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        ScheduledTime before = new ScheduledTime(DateTime.now(DateTimeZone.UTC));
        Mockito.when(req.getParameter("time")).thenReturn(null);
        ScheduledTime t = srv.getRequestTime(req);
        ScheduledTime after = new ScheduledTime(DateTime.now(DateTimeZone.UTC));
        Assert.assertTrue(before.compareTo(t) <= 0);
        Assert.assertTrue(t.compareTo(after) <= 0);
    }

    
}
