package com.collective.celos.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.WorkflowInfo;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.collective.celos.WorkflowID;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/**
 * Returns JS-config file contents for particular workflow-id
 * 
 * GET /workflow-info?id=workflow-1
 * ==>
 *
 *  addWorkflow({
 *     "id": "workflow-1",
 *      "schedule": hourlySchedule(),
 *      "schedulingStrategy": serialSchedulingStrategy(),
 *      "trigger": hdfsCheckTrigger("foo", "file:///"),
 *      "externalService": oozieExternalService({}, "oj01/oozie"),
 *      "maxRetryCount": 0
 *  });
 *
 *
 */
@SuppressWarnings("serial")
public class WorkflowInfoServlet extends AbstractJSONServlet {

    private static final String ID_PARAM = "id";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        String id = req.getParameter(ID_PARAM);
        try {
            if (id == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, ID_PARAM + " parameter missing.");
                return;
            }
            WorkflowID workflowId = new WorkflowID(id);
            WorkflowInfo info = getOrCreateCachedScheduler().getWorkflowConfiguration().getWorkflowInfo(workflowId);
            if (info == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Workflow info for " + id + " was not found");
            } else {
                ObjectNode node = workflowInfoToNode(info);
                writer.writeValue(res.getOutputStream(), node);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private ObjectNode workflowInfoToNode(WorkflowInfo info) throws IOException {
        ObjectNode node = mapper.createObjectNode();
        node.put("contacts", mapper.valueToTree(info.getContacts()));

        if (info.getURL() != null) {
            node.put("url", mapper.valueToTree(info.getURL()));
        }

        String jsFile = FileUtils.readFileToString(info.getWorkflowJSFile());
        node.put("jsFile", jsFile);

        return node;
    }


}
