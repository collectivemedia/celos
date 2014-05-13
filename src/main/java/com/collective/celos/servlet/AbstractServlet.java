package com.collective.celos.servlet;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.SchedulerConfiguration;

/**
 * Superclass for all servlets that access the database.
 */
@SuppressWarnings("serial")
public abstract class AbstractServlet extends HttpServlet {

    private static final String TIME_PARAM = "time";
    private static Logger LOGGER = Logger.getLogger(AbstractServlet.class);
    
    private static final String SCHEDULER_ATTR = "celos.scheduler";

    /**
     * This lock serves to synchronize all operations.
     * 
     * Write operations (scheduler step, rerunning) take the write lock, whereas the informative
     * API servlets take a read lock.
     * 
     * This AbstractServlet overrides the doGet and doPost methods to use the lock and delegate
     * to handleGet and handlePost, which are implemented by subclasses.
     */
    protected static final ReadWriteLock LOCK = new ReentrantReadWriteLock(true);
    protected static final Lock READ_LOCK = LOCK.readLock();
    protected static final Lock WRITE_LOCK = LOCK.writeLock();

    protected void handleGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        throw new Error("GET not supported by servlet.");
    }
    
    protected void handlePost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        throw new Error("POST not supported by servlet.");
    }
    
    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        READ_LOCK.lock();
        try {
            handleGet(req, res);
        } catch(ServletException|IOException|RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            READ_LOCK.unlock();
        }
    }
    
    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        WRITE_LOCK.lock();
        try {
            handlePost(req, res);
        } catch(ServletException|IOException|RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            WRITE_LOCK.unlock();
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
        Scheduler sch = new SchedulerConfiguration().makeDefaultScheduler();
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
