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

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            ScheduledTime time = getRequestTime(req);
            String id = req.getParameter(ID_PARAM);
            if (id == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, ID_PARAM + " parameter missing.");
                return;
            }
            SlotID slot = new SlotID(new WorkflowID(id), time);
            StateDatabase db = getOrCreateCachedScheduler().getStateDatabase();
            SlotState state = db.getSlotState(slot);
            if (state == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Slot not found: " + slot);
                return;
            }
            updateSlotToRerun(state, db);
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
