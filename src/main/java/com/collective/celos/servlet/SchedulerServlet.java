package com.collective.celos.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.collective.celos.DefaultConfiguration;
import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;

/**
 * Posting to this servlet triggers a scheduler step.
 */
@SuppressWarnings("serial")
public class SchedulerServlet extends AbstractServlet {
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            Scheduler scheduler = DefaultConfiguration.makeDefaultScheduler();
            ScheduledTime current = new ScheduledTime(DateTime.now(DateTimeZone.UTC));
            scheduler.step(current);
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

}
