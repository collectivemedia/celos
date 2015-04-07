package com.collective.celos.servlet;

import com.collective.celos.Scheduler;
import com.collective.celos.Workflow;
import com.collective.celos.WorkflowConfiguration;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Returns list of IDs of configured workflows as JSON.
 *
 * GET /workflow-list
 * ==>
 * {
 *   "ids": [ "workflow-1", "workflow-2" ]
 * }
 */
@SuppressWarnings("serial")
public class RrmanServlet extends AbstractJSONServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        res.setContentType("text/html");

        try {
            File src = new File(Thread.currentThread().getContextClassLoader().getResource("RrMan.html").toURI());
            IOUtils.copy(new FileInputStream(src), res.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
