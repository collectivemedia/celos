package com.collective.celos.servlet;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.WorkflowID;

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
public class RerunServlet extends AbstractServlet {
    
    private static final String ID_PARAM = "id";

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            final ScheduledTime time = getRequestTime(req);
            final ScheduledTime current = ScheduledTime.now();
            final String wfIdString = req.getParameter(ID_PARAM);
            if (wfIdString == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, ID_PARAM + " parameter missing.");
                return;
            }
            final WorkflowID wf = new WorkflowID(wfIdString);
            final SlotID id1 = new SlotID(wf, time);
            final boolean status = getOrCreateCachedScheduler().getStateDatabase().updateSlotToRerun(id1, current);
            if (!status) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Slot not found: " + id1);
            }
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

}
