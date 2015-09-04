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

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            String id = req.getParameter(ID_PARAM);
            if (id == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, ID_PARAM + " parameter missing.");
                return;
            }
            Scheduler scheduler = getOrCreateCachedScheduler();
            Workflow wf = scheduler.getWorkflowConfiguration().findWorkflow(new WorkflowID(id));
            if (wf == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Workflow not found: " + id);
                return;
            }
            TriggerStatus status = wf.getTrigger().getTriggerStatus(scheduler, ScheduledTime.now(), getRequestTime(req));
            writer.writeValue(res.getOutputStream(), status);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
}
