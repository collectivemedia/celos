package com.collective.celos.servlet;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.SchedulerConfiguration;
import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * Superclass for all servlets that access the database.
 * 
 * Serializes all database accesses with a lock.
 */
@SuppressWarnings("serial")
public abstract class AbstractServlet extends HttpServlet {

    private static final String TIME_PARAM = "time";
    private static Logger LOGGER = Logger.getLogger(AbstractServlet.class);

    public static final String WORKFLOW_CONFIGURATION_PATH_ATTR = "workflow.configuration.path";
    public static final String DEFAULTS_CONFIGURATION_PATH_ATTR = "defaults.configuration.path";
    public static final String STATE_DATABASE_PATH_ATTR = "state.database.path";
    public static final String ADDITIONAL_JS_VARIABLES = "additional.js.variables";

    private static final String SCHEDULER_ATTR = "celos.scheduler";

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

    protected Scheduler createAndCacheScheduler() throws Exception {
        String workflowConfigPath = getServletContext().getInitParameter(WORKFLOW_CONFIGURATION_PATH_ATTR);
        String defaultsConfigPath = getServletContext().getInitParameter(DEFAULTS_CONFIGURATION_PATH_ATTR);
        String stateDatabasePath = getServletContext().getInitParameter(STATE_DATABASE_PATH_ATTR);
        Map<String, String> additionalVars = (Map<String, String>) getServletContext().getAttribute(ADDITIONAL_JS_VARIABLES);
        if (additionalVars == null) {
            additionalVars = ImmutableMap.of();
        }

        Scheduler sch = new SchedulerConfiguration(workflowConfigPath, defaultsConfigPath, stateDatabasePath, additionalVars).makeDefaultScheduler();
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
