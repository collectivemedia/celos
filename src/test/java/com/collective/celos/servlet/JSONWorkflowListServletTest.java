package com.collective.celos.servlet;

import com.collective.celos.server.ServerConfig;
import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.WorkflowConfiguration;
import com.collective.celos.WorkflowConfigurationParserTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONWorkflowListServletTest {

    @Test
    public void jsonCorrectlyProduced() throws Exception {
        WorkflowConfiguration cfg =
                WorkflowConfigurationParserTest.parseDir("json-workflow-list-servlet-test");
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode list = mapper.createArrayNode();
        list.add(new String("workflow-1"));
        list.add(new String("workflow-2"));
        ObjectNode obj = mapper.createObjectNode();
        obj.put("ids", list);
        Assert.assertEquals(obj, new JSONWorkflowListServlet(new ServerConfig()).createJSONObject(cfg));
    }
    
}
