package com.collective.celos.servlet;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.Workflow;
import com.collective.celos.WorkflowID;
import com.collective.celos.trigger.Trigger;
import com.collective.celos.trigger.TriggerStatus;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Posting to this servlet reruns the specified slot.
 * 
 * Parameters:
 * 
 * id -- workflow ID
 * 
 * time -- scheduled time of slot
 */
@SuppressWarnings("serial")
public class TriggerStatusServlet extends AbstractJSONServlet {
    
    private static Logger LOGGER = Logger.getLogger(TriggerStatusServlet.class);

    protected static final String ID_PARAM = "id";
//    private static final String TIME_PARAM = "time";

    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException {
        try {
            String id = req.getParameter(ID_PARAM);
            if (id == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ID_PARAM + " parameter missing.");
                return;
            }
            Scheduler scheduler = getOrCreateCachedScheduler();
            Workflow wf = scheduler.getWorkflowConfiguration().findWorkflow(new WorkflowID(id));
            if (wf == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Workflow not found: " + id);
                return;
            }
            final String status = getStatus(req, response, scheduler, wf);
            if (status != null) {
                response.getOutputStream().write(status.getBytes());
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected String getStatus(HttpServletRequest req, HttpServletResponse response, Scheduler scheduler, Workflow wf) throws Exception {
        ScheduledTime time = getRequestTime(req);
        ScheduledTime now = ScheduledTime.now();
        final Trigger trigger = wf.getTrigger();
        final TriggerStatus statusObject = trigger.getTriggerStatus(scheduler, now, time);
        // convert user object to json string, and save to a file
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(statusObject);
    }
}
