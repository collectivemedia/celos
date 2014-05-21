package com.collective.celos.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.*;
import org.apache.log4j.Logger;

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
    private static final String RERUN_PARAM = "rerunDependentWorkflows";

    
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            ScheduledTime time = getRequestTime(req);
            String id = req.getParameter(ID_PARAM);
            if (id == null) {
                throw new IllegalArgumentException(ID_PARAM + " parameter missing.");
            }
            final Scheduler scheduler = getOrCreateCachedScheduler();
            final WorkflowID workflowID = new WorkflowID(id);

            rerunWorkflow(time, scheduler, workflowID, rerunNeedToBeChained(req));
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

    private boolean rerunNeedToBeChained(HttpServletRequest req) {
        return Boolean.TRUE.toString().equalsIgnoreCase(req.getParameter(RERUN_PARAM));
    }

    private void rerunWorkflow(ScheduledTime time, Scheduler scheduler, WorkflowID workflowId, boolean rerunDependent) throws Exception {
        SlotID slot = new SlotID(workflowId, time);
        StateDatabase db = scheduler.getStateDatabase();
        updateSlotToRerun(slot, db);
        LOGGER.info("Slot scheduled for rerun: " + slot);
        if (rerunDependent) {
            for (WorkflowID depId : scheduler.getWorkflowConfiguration().getDependentWorkflows(workflowId)) {
                rerunWorkflow(time, scheduler, depId, rerunDependent);
            }
        }
    }

    void updateSlotToRerun(SlotID slot, StateDatabase db) throws Exception {
        SlotState state = db.getSlotState(slot);
        if (state == null) {
            throw new IllegalArgumentException("Slot not found: " + slot);
        }
        SlotState newState = state.transitionToRerun();
        db.putSlotState(newState);
    }

}
