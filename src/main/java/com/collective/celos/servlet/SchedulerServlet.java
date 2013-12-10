package com.collective.celos.servlet;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.collective.celos.FileSystemStateDatabase;
import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.StateDatabase;
import com.collective.celos.WorkflowConfiguration;
import com.collective.celos.WorkflowConfigurationParser;

@SuppressWarnings("serial")
public class SchedulerServlet extends HttpServlet {
    
    /**
     * This lock serves to synchronize all scheduler operations.
     */
    public static final Object LOCK = new Object();

    private static final String CONFIG_PATH = "/etc/celos/workflows";
    private static final String DB_PATH = "/var/run/celos/db";

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        synchronized(LOCK) {
            try {
                File configFile = new File(CONFIG_PATH);
                WorkflowConfiguration config =
                        new WorkflowConfigurationParser().parseConfiguration(configFile);
                StateDatabase db = new FileSystemStateDatabase(new File(DB_PATH));
                int slidingWindowHours = 24 * 7;
                ScheduledTime current = new ScheduledTime(DateTime.now(DateTimeZone.UTC));
                new Scheduler(config, db, slidingWindowHours).step(current);
            } catch(Exception e) {
                throw new ServletException(e);
            }
        }
    }

}
