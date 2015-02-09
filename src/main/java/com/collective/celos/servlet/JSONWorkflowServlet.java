package com.collective.celos.servlet;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.Scheduler;
import com.collective.celos.SlotState;
import com.collective.celos.Workflow;
import com.collective.celos.WorkflowID;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Returns information about the slot states of a single workflow as JSON.
 * 
 * GET /workflow?id=workflow-1
 * ==>
 * {
 *   "slots": {
 *     "2013-12-07T13:00:00.000Z": { "status": "RUNNING", "externalID": "237982137-371832798321-W", retryCount: 5 },
 *     "2013-12-07T14:00:00.000Z": { "status": "READY", "externalID": null, retryCount: 0 },
 *     ...
 *   }
 * }
 * 
 * If the "time" parameter is supplied, information is returned about 
 * slot states up to that time.
 */
@SuppressWarnings("serial")
public class JSONWorkflowServlet extends AbstractJSONServlet {

    private static final String ID_PARAM = "id";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        String id = req.getParameter(ID_PARAM);
        try {
            if (id == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, ID_PARAM + " parameter missing.");
                return;
            }
            Scheduler scheduler = getOrCreateCachedScheduler();
            Workflow wf = scheduler.getWorkflowConfiguration().findWorkflow(new WorkflowID(id));
            if (wf == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Workflow not found: " + id);
            } else {
                List<SlotState> slotStates = scheduler.getSlotStates(wf, getRequestTime(req));
                ObjectNode object = createJSONObject(slotStates);
                writer.writeValue(res.getOutputStream(), object);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    ObjectNode createJSONObject(List<SlotState> slotStates) {
        ObjectNode node = mapper.createObjectNode();
        for (SlotState state : slotStates) {
            node.put(state.getScheduledTime().toString(), state.toJSONNode());
        }
        return node;
    }

}
