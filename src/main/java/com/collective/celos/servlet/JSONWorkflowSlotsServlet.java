package com.collective.celos.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

/**
 * Returns information about the slot states of one or more workflows as JSON.
 *
 * If no id parameter is specified, returns slot states of all workflows.
 *
 * If the id parameter is specified, returns only slot states of that workflow.
 *
 * GET /workflow-slots
 * ==>
 * {
 *     "workflows" : [
 *     {
 *       "id": "workflow-1",
 *       "info": {
 *           "url": "http://myurl",
 *           "contacts": [
 *               { "name": "John Doe", "email": "John.Doe@Gmail.Com"},
 *               { "name": "Jack Smith", "email": "Jack.Smith@Gmail.Com"},
 *           ]
 *       },
 *       "slots": [
 *          { "time": "2013-12-07T13:00:00.000Z", "status": "RUNNING", "externalID": "237982137-371832798321-W", retryCount: 5 },
 *          { "time": "2013-12-07T14:00:00.000Z", "status": "READY", "externalID": null, retryCount: 0 },
 *         ...
 *       ]
 *     }, {
 *       "id": "workflow-2",
 *       "info": {
 *           "url": "http://myurl2",
 *           "contacts": [
 *               { "name": "Ivan Ivanov", "email": "ivan.ivanov@Gmail.Com"},
 *               { "name": "Ivan Petrov", "email": "ivan.petrov@Gmail.Com"},
 *           ]
 *       },
 *       "slots": [
 *          { "time": "2013-12-07T13:00:00.000Z", "status": "RUNNING", "externalID": "237982137-371832798322-W", retryCount: 2 },
 *          { "time": "2013-12-07T14:00:00.000Z", "status": "READY", "externalID": null, retryCount: 0 },
 *         ...
 *       ]
 *     }
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
            Scheduler scheduler = getOrCreateCachedScheduler();
            WorkflowConfiguration cfg = scheduler.getWorkflowConfiguration();

            Collection<Workflow> workflows = getWorkflows(id, cfg);
            if (workflows == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Workflow not found: " + id);
            } else {
                ArrayNode list = mapper.createArrayNode();
                ObjectNode object = mapper.createObjectNode();

                for (Workflow wf : workflows) {
                    ObjectNode node = mapper.createObjectNode();
                    List<SlotState> slotStates = scheduler.getSlotStates(wf, getRequestTime(req));
                    List<JsonNode> objectNodes = Lists.newArrayList();
                    for (SlotState state : Lists.reverse(slotStates)) {
                        objectNodes.add(state.toJSONNode());
                    }
                    node.put(ID_PARAM, wf.getID().toString());
                    node.put(INFO_PARAM, mapper.valueToTree(wf.getWorkflowInfo()));
                    node.putArray(SLOTS_PARAM).addAll(objectNodes);
                    list.add(node);
                }
                object.put("workflows", list);
                writer.writeValue(res.getOutputStream(), object);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private Collection<Workflow> getWorkflows(String id, WorkflowConfiguration cfg) throws IOException {
        Collection<Workflow> workflows;
        if (id == null) {
            workflows = cfg.getWorkflows();
        } else {
            Workflow wf = cfg.findWorkflow(new WorkflowID(id));
            if (wf == null) {
                workflows = null;
            } else {
                workflows = Arrays.asList(wf);
            }
        }
        return workflows;
    }

}
