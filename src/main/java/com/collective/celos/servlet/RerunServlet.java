package com.collective.celos.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SchedulerConfiguration;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.StateDatabase;
import com.collective.celos.WorkflowID;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Posting to this servlet reruns the specified slot.
 *
 * POST /rerun
 *
 * Parameters:
 * 
 * id -- workflow ID. Example: "foo-bar-workflow"
 * 
 * time -- scheduled time of slot in Zulu time format. Example: "2014-02-25T22:00:00.000Z"
 *
 * Returns updated state of the slot
 */
@SuppressWarnings("serial")
public class RerunServlet extends AbstractJSONServlet {
    
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
            SlotState slotState = updateSlotToRerun(slot, db);

            ObjectNode node = getJSONNode(slotState);
            writer.writeValue(res.getOutputStream(), node);
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

    private ObjectNode getJSONNode(SlotState slotState) {
        ObjectNode node = mapper.createObjectNode();
        node.put(slotState.getScheduledTime().toString(), slotState.toJSONNode());
        return node;
    }

    SlotState updateSlotToRerun(SlotID slot, StateDatabase db) throws Exception {
        SlotState state = db.getSlotState(slot);
        if (state == null) {
            throw new IllegalArgumentException("Slot not found: " + slot);
        }
        SlotState newState = state.transitionToRerun();
        db.putSlotState(newState);
        return newState;
    }

}
