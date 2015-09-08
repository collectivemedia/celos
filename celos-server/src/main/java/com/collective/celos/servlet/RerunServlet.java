package com.collective.celos.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.*;
import org.apache.log4j.Logger;

import java.util.Set;

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
public class RerunServlet extends AbstractServlet {
    
    private static Logger LOGGER = Logger.getLogger(RerunServlet.class);
    
    private static final String ID_PARAM = "id";

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            ScheduledTime time = getRequestTime(req);
            String id = req.getParameter(ID_PARAM);
            if (id == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, ID_PARAM + " parameter missing.");
                return;
            }

            Scheduler scheduler = getOrCreateCachedScheduler();
            WorkflowID workflowID = new WorkflowID(id);
            Workflow workflow = scheduler.getWorkflowConfiguration().findWorkflow(workflowID);
            if (workflow == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Workflow not found: " + id);
                return;
            }

            SlotID slot = new SlotID(workflowID, time);
            Set<ScheduledTime> timeSet = workflow.getSchedule().getScheduledTimes(scheduler, time.minusSeconds(1), time.plusSeconds(1));
            if (!timeSet.contains(time)) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Slot is not found: " + slot);
                return;
            }

            StateDatabase db = scheduler.getStateDatabase();
            SlotState state = db.getSlotState(slot);
            if (state != null) {
                updateSlotToRerun(state, db);
            }
            db.markSlotForRerun(slot, ScheduledTime.now());
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

    void updateSlotToRerun(SlotState state, StateDatabase db) throws Exception {
        LOGGER.info("Scheduling Slot for rerun: " + state.getSlotID());
        SlotState newState = state.transitionToRerun();
        db.putSlotState(newState);
    }

}
