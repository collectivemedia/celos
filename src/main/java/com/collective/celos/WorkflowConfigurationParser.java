package com.collective.celos;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reads a set of JSON files from a directory and creates a WorkflowConfiguration.
 */
public class WorkflowConfigurationParser {

    private static final String EXTERNAL_SERVICE_PROP = "externalService";
    private static final String TRIGGER_PROP = "trigger";
    private static final String SCHEDULING_STRATEGY_PROP = "schedulingStrategy";
    private static final String SCHEDULE_PROP = "schedule";
    private static final String ID_PROP = "id";
    private static final String MAX_RETRY_COUNT_PROP = "maxRetryCount";

    private static final Logger LOGGER = Logger.getLogger(WorkflowConfigurationParser.class);
    
    private final JSONInstanceCreator creator = new JSONInstanceCreator();
    
    public WorkflowConfiguration parseConfiguration(File dir) throws Exception {
        LOGGER.info("Workflow configuration directory: " + dir);
        Collection<File> files = FileUtils.listFiles(dir, new String[] { "json" }, false);
        WorkflowConfiguration cfg = new WorkflowConfiguration();
        for (File f : files) {
            try {
                cfg.addWorkflow(parseFile(f));
            } catch(Exception e) {
                LOGGER.error("Failed to load workflow: " + f, e);
            }
        }
        return cfg;
    }

    Workflow parseFile(File f) throws Exception {
        LOGGER.info("Loading workflow: " + f);
        JsonNode workflowNode = new ObjectMapper().readTree(f);
        WorkflowID id = getWorkflowID(workflowNode);
        Schedule schedule = getScheduleFromJSON(id, workflowNode);
        SchedulingStrategy schedulingStrategy = getSchedulingStrategyFromJSON(id, workflowNode);
        Trigger trigger = getTriggerFromJSON(id, workflowNode);
        ExternalService externalService = getExternalServiceFromJSON(id, workflowNode);
        int maxRetryCount = getMaxRetryCountFromJSON(workflowNode);
        return new Workflow(id, schedule, schedulingStrategy, trigger, externalService, maxRetryCount);
    }

    private int getMaxRetryCountFromJSON(JsonNode workflowNode) {
        JsonNode maxRetryCountNode = workflowNode.get(MAX_RETRY_COUNT_PROP);
        if (!maxRetryCountNode.isNumber()) {
            throw new IllegalArgumentException("maxRetryCount must be a number: " + workflowNode.toString());
        }
        return maxRetryCountNode.intValue();
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
