package com.collective.celos;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collection;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Reads a set of JSON files from a directory and creates a WorkflowConfiguration.
 */
public class WorkflowConfigurationParser {

    private static final String PROPERTIES_PROP = "properties";
    private static final String TYPE_PROP = "type";
    private static final String EXTERNAL_SERVICE_PROP = "externalService";
    private static final String TRIGGER_PROP = "trigger";
    private static final String SCHEDULING_STRATEGY_PROP = "schedulingStrategy";
    private static final String SCHEDULE_PROP = "schedule";
    private static final String ID_PROP = "id";
    private static final String MAX_RETRY_COUNT_PROP = "maxRetryCount";

    private static final Logger LOGGER = Logger.getLogger(WorkflowConfigurationParser.class);
    
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
        return (ExternalService) createInstance(id, workflowNode.get(EXTERNAL_SERVICE_PROP));
    }

    private Trigger getTriggerFromJSON(WorkflowID id, JsonNode workflowNode) throws Exception {
        return (Trigger) createInstance(id, workflowNode.get(TRIGGER_PROP));
    }

    private SchedulingStrategy getSchedulingStrategyFromJSON(WorkflowID id, JsonNode workflowNode) throws Exception {
        return (SchedulingStrategy) createInstance(id, workflowNode.get(SCHEDULING_STRATEGY_PROP));
    }

    private Schedule getScheduleFromJSON(WorkflowID id, JsonNode workflowNode) throws Exception {
        return (Schedule) createInstance(id, workflowNode.get(SCHEDULE_PROP));
    }

    private WorkflowID getWorkflowID(JsonNode workflowNode) {
        JsonNode idNode = workflowNode.get(ID_PROP);
        if (idNode == null || !idNode.isTextual()) {
            throw new IllegalArgumentException("ID must be a string: " + workflowNode.toString());
        }
        return new WorkflowID(idNode.textValue());
    }

    private Object createInstance(WorkflowID id, JsonNode node) throws Exception {
        ObjectNode properties = getProperties(node);
        Constructor<?> ctor = getConstructor(node);
        LOGGER.info("Instantiating " + ctor + " for: " + id);
        return ctor.newInstance(properties);
    }

    ObjectNode getProperties(JsonNode node) {
        JsonNode properties = node.get(PROPERTIES_PROP);
        if (properties == null) {
            return Util.newObjectNode();
        } else {
            if (!properties.isObject()) {
                throw new IllegalArgumentException("Properties must be an object, but is: " + properties);
            } else {
                return (ObjectNode) properties;
            }
        }
    }

    private Constructor<?> getConstructor(JsonNode workflowNode) throws ClassNotFoundException {
        JsonNode typeNode = workflowNode.get(TYPE_PROP);
        if (typeNode == null || !typeNode.isTextual()) {
            throw new IllegalArgumentException("Type must be a string: " + workflowNode.toString());
        }
        String className = typeNode.textValue();
        Class<?> c = Class.forName(className);
        Constructor<?> ctor = ConstructorUtils.getAccessibleConstructor(c, ObjectNode.class);
        if (ctor == null) {
            throw new RuntimeException("Constructor with Properties argument not found for " + className);
        }
        return ctor;
    }

}
