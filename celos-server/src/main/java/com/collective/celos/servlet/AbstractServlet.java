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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.*;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.collective.celos.database.StateDatabase;
import com.google.common.collect.ImmutableMap;

/**
 * Superclass for all servlets that access the database.
 *
 * Serializes all database accesses with a lock.
 */
@SuppressWarnings("serial")
public abstract class AbstractServlet extends HttpServlet {

    private static final String TIME_PARAM = "time";
    private static Logger LOGGER = Logger.getLogger(AbstractServlet.class);
    public static final String SCHEDULER_ATTR = "celos.scheduler";
    
    /**
     * This lock serves to synchronize all operations.
     */
    protected static final Object LOCK = new Object();

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        synchronized(LOCK) {
            try {
                super.service(req, res);
            } catch(ServletException|IOException|RuntimeException e) {
                LOGGER.error(e.getMessage(), e);
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getLocalizedMessage());
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

    /*
     * Scheduler cache
     *
     * Reading and evaluating the .js files takes a bit of time, so it is cached
     * between servlet calls.
     *
     * The scheduler servlet uses createAndCacheScheduler(), which refreshes
     * the cache once a minute, whereas all other servlets use, for performance,
     * getOrCreateCachedScheduler() which usually retrieves the cached
     * scheduler. The clear-cache servlet can be used during e.g. integration testing
     * to reset the cache and force a reload of the configuration.
     */

    private SchedulerConfiguration getSchedulerConfiguration() throws IOException {
        String workflowConfigPath = Util.requireNonNull(getServletContext().getInitParameter(Constants.WORKFLOW_CONFIGURATION_PATH_ATTR));
        String defaultsConfigPath = Util.requireNonNull(getServletContext().getInitParameter(Constants.DEFAULTS_CONFIGURATION_PATH_ATTR));
        StateDatabase.Config config = (StateDatabase.Config) Util.requireNonNull(getServletContext().getAttribute(Constants.DATABASE_CONFIG));

        Map<String, String> additionalVars = (Map<String, String>) getServletContext().getAttribute(Constants.ADDITIONAL_JS_VARIABLES);
        if (additionalVars == null) {
            additionalVars = ImmutableMap.of();
        }
        SchedulerConfiguration schedulerConfiguration = new SchedulerConfiguration(
                new File(workflowConfigPath), new File(defaultsConfigPath), config, additionalVars
        );
        return schedulerConfiguration;
    }

    protected Scheduler createAndCacheScheduler() throws Exception {
        SchedulerConfiguration schedulerConfiguration = getSchedulerConfiguration();
        Scheduler sch = schedulerConfiguration.makeDefaultScheduler();
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

    protected StateDatabase getStateDatabase() throws IOException {
        return getSchedulerConfiguration().getStateDatabase();
    }
    
}
