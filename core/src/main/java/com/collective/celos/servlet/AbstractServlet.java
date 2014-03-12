package com.collective.celos.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.collective.celos.api.ScheduledTime;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Superclass for all servlets that access the database.
 * 
 * Serializes all database accesses with a lock.
 */
@SuppressWarnings("serial")
public abstract class AbstractServlet extends HttpServlet {

    private static final String TIME_PARAM = "time";
    private static Logger LOGGER = Logger.getLogger(AbstractServlet.class);
    
    /**
     * This lock serves to synchronize all operations.
     */
    protected static final Object LOCK = new Object();

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

}
