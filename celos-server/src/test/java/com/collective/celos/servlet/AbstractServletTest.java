package com.collective.celos.servlet;

import com.collective.celos.ScheduledTime;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

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
