package com.collective.celos;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reads a set of JS files from a directory and creates a WorkflowConfiguration.
 * 
 * The JS engine has one variable defined, celosWorkflowConfigurationParser, which
 * points to the parser instance.
 * 
 * The helper script celos-scripts.js defines the utility function addWorkflow(object),
 * which stringifies the input JS object, and passes the string to the parser's
 * addWorkflowFromJSONString method.
 */
public class WorkflowConfigurationParser {

    public static final String WORKFLOW_FILE_EXTENSION = "js";
    
    private static final String EXTERNAL_SERVICE_PROP = "externalService";
    private static final String TRIGGER_PROP = "trigger";
    private static final String SCHEDULING_STRATEGY_PROP = "schedulingStrategy";
    private static final String SCHEDULE_PROP = "schedule";
    private static final String ID_PROP = "id";
    private static final String MAX_RETRY_COUNT_PROP = "maxRetryCount";
    private static final String START_TIME_PROP = "startTime";

    private static final Logger LOGGER = Logger.getLogger(WorkflowConfigurationParser.class);
    
    private final JSONInstanceCreator creator = new JSONInstanceCreator();
    private final ObjectMapper mapper = new ObjectMapper();
    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    private final WorkflowConfiguration cfg = new WorkflowConfiguration();;

    public WorkflowConfigurationParser(File dir) throws Exception {
        InputStream scripts = WorkflowConfigurationParser.class.getResourceAsStream("celos-scripts.js");
        engine.eval(new InputStreamReader(scripts));
        engine.put("celosWorkflowConfigurationParser", this);
        parseConfiguration(dir);
    }
    
    public void parseConfiguration(File dir) {
        LOGGER.info("Workflow configuration directory: " + dir);
        Collection<File> files = FileUtils.listFiles(dir, new String[] { WORKFLOW_FILE_EXTENSION }, false);
        for (File f : files) {
            try {
                LOGGER.info("Evaluating file: " + f);
                parseFile(f);
            } catch(Exception e) {
                LOGGER.error("Failed to load workflow: " + f, e);
            }
        }
    }

    public void parseFile(File f) throws Exception {
        engine.eval(IOUtils.toString(new FileReader(f)));
    }

    public WorkflowConfiguration getWorkflowConfiguration() {
        return cfg;
    }
    
    public void addWorkflowFromJSONString(String json) throws Exception {
        JsonNode workflowNode = mapper.readTree(json);
        WorkflowID id = getWorkflowID(workflowNode);
        Schedule schedule = getScheduleFromJSON(id, workflowNode);
        SchedulingStrategy schedulingStrategy = getSchedulingStrategyFromJSON(id, workflowNode);
        Trigger trigger = getTriggerFromJSON(id, workflowNode);
        ExternalService externalService = getExternalServiceFromJSON(id, workflowNode);
        int maxRetryCount = getMaxRetryCountFromJSON(workflowNode);
        ScheduledTime startTime = getStartTimeFromJSON(workflowNode);
        cfg.addWorkflow(new Workflow(id, schedule, schedulingStrategy, trigger, externalService, maxRetryCount, startTime));
    }
    
    private int getMaxRetryCountFromJSON(JsonNode workflowNode) {
        JsonNode maxRetryCountNode = workflowNode.get(MAX_RETRY_COUNT_PROP);
        if (!maxRetryCountNode.isNumber()) {
            throw new IllegalArgumentException("maxRetryCount must be a number: " + workflowNode.toString());
        }
        return maxRetryCountNode.intValue();
    }

    private ScheduledTime getStartTimeFromJSON(JsonNode workflowNode) {
        ScheduledTime start;
        JsonNode startTimeNode = workflowNode.get(START_TIME_PROP);
        if (startTimeNode == null) {
            start = Workflow.DEFAULT_START_TIME;
        } else if (!startTimeNode.isTextual()) {
            throw new IllegalArgumentException("startTime must be a string: " + workflowNode.toString());
        } else {
            start = new ScheduledTime(startTimeNode.textValue());
        }
        return start;
    }

    private ExternalService getExternalServiceFromJSON(WorkflowID id, JsonNode workflowNode) throws Exception {
        LOGGER.info("Creating external service for: " + id);
        return (ExternalService) createInstance(workflowNode.get(EXTERNAL_SERVICE_PROP));
    }

    private Trigger getTriggerFromJSON(WorkflowID id, JsonNode workflowNode) throws Exception {
        LOGGER.info("Creating trigger for: " + id);
        return (Trigger) createInstance(workflowNode.get(TRIGGER_PROP));
    }

    private SchedulingStrategy getSchedulingStrategyFromJSON(WorkflowID id, JsonNode workflowNode) throws Exception {
        LOGGER.info("Creating scheduling strategy for: " + id);
        return (SchedulingStrategy) createInstance(workflowNode.get(SCHEDULING_STRATEGY_PROP));
    }

    private Schedule getScheduleFromJSON(WorkflowID id, JsonNode workflowNode) throws Exception {
        LOGGER.info("Creating schedule for: " + id);
        return (Schedule) createInstance(workflowNode.get(SCHEDULE_PROP));
    }

    private Object createInstance(JsonNode jsonNode) throws Exception {
        return creator.createInstance(jsonNode);
    }

    private WorkflowID getWorkflowID(JsonNode workflowNode) {
        JsonNode idNode = workflowNode.get(ID_PROP);
        if (idNode == null || !idNode.isTextual()) {
            throw new IllegalArgumentException("ID must be a string: " + workflowNode.toString());
        }
        return new WorkflowID(idNode.textValue());
    }

}
