package com.collective.celos.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.server.ServerConfig;

/**
 * Posting to this servlet triggers a scheduler step.
 * 
 * If the "time" parameter is supplied, the step is performed for that time. 
 */
@SuppressWarnings("serial")
public class SchedulerServlet extends AbstractServlet {

    public SchedulerServlet(ServerConfig celosServer) {
        super(celosServer);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            Scheduler scheduler = createAndCacheScheduler();
            ScheduledTime current = getRequestTime(req);
            scheduler.step(current);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
