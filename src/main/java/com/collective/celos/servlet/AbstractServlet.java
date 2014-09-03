package com.collective.celos.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.collective.celos.server.ServerConfig;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.SchedulerConfiguration;

/**
 * Superclass for all servlets that access the database.
 * 
 * Serializes all database accesses with a lock.
 */
@SuppressWarnings("serial")
public abstract class AbstractServlet extends HttpServlet {

    private static final String TIME_PARAM = "time";
    private static Logger LOGGER = Logger.getLogger(AbstractServlet.class);
    
    private static final String SCHEDULER_ATTR = "celos.scheduler";

    /**
     * This lock serves to synchronize all operations.
     */
    protected static final Object LOCK = new Object();

    /**
     * that holds configuration preferences
     */
    private ServerConfig serverConfig;

    public AbstractServlet(ServerConfig celosServer) {
        this.serverConfig = celosServer;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        synchronized(LOCK) {
            try {
                super.service(req, res);
            } catch(ServletException|IOException|RuntimeException e) {
                LOGGER.error(e.getMessage(), e);
                throw e;
            }
        }
    }
    
    protected ScheduledTime getRequestTime(HttpServletRequest req) {
        String t = req.getParameter(TIME_PARAM);
        if (t == null) {
            return new ScheduledTime(DateTime.now(DateTimeZone.UTC));
        } else {
            return new ScheduledTime(t);
        }
    }

    protected Scheduler createAndCacheScheduler() throws Exception {
        Scheduler sch = new SchedulerConfiguration(serverConfig).makeDefaultScheduler();
        getServletContext().setAttribute(SCHEDULER_ATTR, sch);
        return sch;
    }
    
    protected Scheduler getOrCreateCachedScheduler() throws Exception {
        Scheduler sch = (Scheduler) getServletContext().getAttribute(SCHEDULER_ATTR);
        if (sch == null) {
            return createAndCacheScheduler();
        } else {
            return sch;
        }
    }
    
    protected void clearSchedulerCache() {
        getServletContext().removeAttribute(SCHEDULER_ATTR);
    }
    
}
