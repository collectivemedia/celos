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

import com.collective.celos.Util;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;

public class AbstractServletTest {

    @Test
    public void getRequestTimeParameterWorks() {
        @SuppressWarnings("serial")
        AbstractServlet srv = new AbstractServlet() {};
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        String timeString = "2013-12-18T20:00Z";
        Mockito.when(req.getParameter("time")).thenReturn(timeString);
        Assert.assertEquals(ZonedDateTime.parse(timeString), srv.getRequestTime(req));
    }
    
    @Test
    public void getRequestTimeUsesCurrentTimeWhenNotSupplied() {
        @SuppressWarnings("serial")
        AbstractServlet srv = new AbstractServlet() {};
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        ZonedDateTime before = Util.zonedDateTimeNowUTC();
        Mockito.when(req.getParameter("time")).thenReturn(null);
        ZonedDateTime t = srv.getRequestTime(req);
        ZonedDateTime after = Util.zonedDateTimeNowUTC();
        Assert.assertTrue(before.compareTo(t) <= 0);
        Assert.assertTrue(t.compareTo(after) <= 0);
    }

    
}
