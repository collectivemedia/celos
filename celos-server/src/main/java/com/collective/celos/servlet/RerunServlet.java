package com.collective.celos.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.StateDatabase;
import com.collective.celos.WorkflowID;

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
            SlotID slotID = new SlotID(new WorkflowID(id), time);
            StateDatabase db = getOrCreateCachedScheduler().getStateDatabase();
            SlotState state = db.getSlotState(slotID);
            if (state == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Slot not found: " + slotID);
                return;
            }
            updateSlotToRerun(slotID, state, db);
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

    void updateSlotToRerun(SlotID slotID, SlotState state, StateDatabase db) throws Exception {
        LOGGER.info("Scheduling Slot for rerun: " + slotID);
        SlotState newState = state.transitionToRerun();
        db.putSlotState(newState);
        db.markSlotForRerun(slotID, ScheduledTime.now());
    }

}
