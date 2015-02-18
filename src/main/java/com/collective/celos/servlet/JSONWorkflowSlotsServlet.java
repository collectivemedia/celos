package com.collective.celos.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

/**
 * Returns information about the slot states of a single workflow as JSON.
 * 
 * GET /workflow-slots?id=workflow-1
 * ==>
 * {
 *   "info": {
 *       "url": "http://myurl",
 *       "contacts": [
 *           { "name": "John Doe", "email": "John.Doe@Gmail.Com"},
 *           { "name": "Jack Smith", "email": "Jack.Smith@Gmail.Com"},
 *       ]
 *   },
 *   "slots": [
 *      { "time": "2013-12-07T13:00:00.000Z", "status": "RUNNING", "externalID": "237982137-371832798321-W", retryCount: 5 },
 *      { "time": "2013-12-07T14:00:00.000Z", "status": "READY", "externalID": null, retryCount: 0 },
 *     ...
 *   ]
 * }
 * 
 * If the "time" parameter is supplied, information is returned about 
 * slot states up to that time.
 */
@SuppressWarnings("serial")
public class JSONWorkflowSlotsServlet extends AbstractJSONServlet {

    private static final String ID_PARAM = "id";
    private static final String INFO_PARAM = "info";
    private static final String SLOTS_PARAM = "slots";

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
                ObjectNode node = mapper.createObjectNode();
                List<SlotState> slotStates = scheduler.getSlotStates(wf, getRequestTime(req));
                List<JsonNode> objectNodes = Lists.newArrayList();
                for (SlotState state : Lists.reverse(slotStates)) {
                    objectNodes.add(state.toJSONNode());
                }
                node.put(INFO_PARAM, mapper.valueToTree(wf.getWorkflowInfo()));
                node.putArray(SLOTS_PARAM).addAll(objectNodes);
                writer.writeValue(res.getOutputStream(), node);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

}
