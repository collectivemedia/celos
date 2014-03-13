package com.collective.celos.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.api.ScheduledTime;
import com.collective.celos.SchedulerConfiguration;
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
    
    private static final String ID_PARAM = "id";
    
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            ScheduledTime time = getRequestTime(req);
            String id = req.getParameter(ID_PARAM);
            if (id == null) {
                throw new IllegalArgumentException(ID_PARAM + " parameter missing.");
            }
            SlotID slot = new SlotID(new WorkflowID(id), time);
            StateDatabase db = new SchedulerConfiguration().makeDefaultStateDatabase();
            updateSlotToRerun(slot, db);
        } catch(Exception e) {
            throw new ServletException(e);
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
