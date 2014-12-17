package com.collective.celos.servlet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.WorkflowID;

/**
 * Posting to this servlet triggers a scheduler step.
 * 
 * If the "time" parameter is supplied, the step is performed for that time. 
 */
@SuppressWarnings("serial")
public class SchedulerServlet extends AbstractServlet {

    public static final String IDS_PARAM = "ids";

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            Scheduler scheduler = createAndCacheScheduler();
            ScheduledTime current = getRequestTime(req);
            Set<WorkflowID> workflowIDs = getWorkflowIDs(req);
            scheduler.step(current, workflowIDs);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    Set<WorkflowID> getWorkflowIDs(HttpServletRequest req) {
        String idString = req.getParameter(IDS_PARAM);
        if (idString == null) {
            return Collections.<WorkflowID>emptySet();
        } else {
            String[] idArray = idString.split(",");
            Set<WorkflowID> workflowIDs = new HashSet<>();
            for (int i = 0; i < idArray.length; i++) {
                workflowIDs.add(new WorkflowID(idArray[i]));
            }
            return workflowIDs;
        }
    }

}
