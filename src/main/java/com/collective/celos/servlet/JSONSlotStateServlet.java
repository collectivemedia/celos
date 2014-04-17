package com.collective.celos.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.Scheduler;
import com.collective.celos.SchedulerConfiguration;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.fasterxml.jackson.databind.node.ObjectNode;

@SuppressWarnings("serial")
public class JSONSlotStateServlet extends AbstractJSONServlet {
    
    private static final String ID_PARAM = "id";
    
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        String id = req.getParameter(ID_PARAM);
        if (id == null) {
            throw new IllegalArgumentException(ID_PARAM + " parameter missing.");
        }
        try {
            Scheduler scheduler = new SchedulerConfiguration().makeDefaultScheduler();
            SlotID slotID = new SlotID(new WorkflowID(id), getRequestTime(req));
            SlotState slotState = scheduler.getStateDatabase().getSlotState(slotID);
            if (slotState == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Slot not found: " + id);
            } else {
                ObjectNode object = slotState.toJSONNode();
                writer.writeValue(res.getOutputStream(), object);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
}
