package com.collective.celos;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public WorkflowConfiguration parseConfiguration(File dir) throws Exception {
        Collection<File> files = FileUtils.listFiles(dir, new String[] { "json" }, false);
        Set<Workflow> workflows = new HashSet<Workflow>();
        for (File f : files) {
            workflows.add(parseFile(f));
        }
        return new WorkflowConfiguration(workflows);
    }

    private Workflow parseFile(File f) throws Exception {
        JsonNode workflowNode = new ObjectMapper().readTree(f);
        WorkflowID id = new WorkflowID(workflowNode.get(ID_PROP).textValue());
        Schedule schedule =
                (Schedule) createInstance(workflowNode.get(SCHEDULE_PROP));
        SchedulingStrategy schedulingStrategy =
                (SchedulingStrategy) createInstance(workflowNode.get(SCHEDULING_STRATEGY_PROP));
        Trigger trigger =
                (Trigger) createInstance(workflowNode.get(TRIGGER_PROP));
        ExternalService externalService =
                (ExternalService) createInstance(workflowNode.get(EXTERNAL_SERVICE_PROP));
        return new Workflow(id, schedule, schedulingStrategy, trigger, externalService);
    }

    private Object createInstance(JsonNode workflowNode) throws Exception {
        Properties properties = getProperties(workflowNode);
        Constructor<?> ctor = getConstructor(workflowNode);
        return ctor.newInstance(properties);
    }

    private Constructor<?> getConstructor(JsonNode workflowNode) throws ClassNotFoundException {
        JsonNode typeNode = workflowNode.get(TYPE_PROP);
        if (typeNode == null || !typeNode.isTextual()) {
            throw new IllegalArgumentException("Type must be a string: " + workflowNode.asText());
        }
        String className = typeNode.textValue();
        Class<?> c = Class.forName(className);
        Constructor<?> ctor = ConstructorUtils.getAccessibleConstructor(c, Properties.class);
        if (ctor == null) {
            throw new RuntimeException("Constructor with Properties argument not found for " + className);
        }
        return ctor;
    }

    Properties getProperties(JsonNode jsonNode) {
        JsonNode propertiesNode = jsonNode.get(PROPERTIES_PROP);
        Properties properties = null;
        if (propertiesNode != null) {
            properties = jsonPropertiesToProperties(propertiesNode);
        } else {
            properties = new Properties();
        }
        return properties;
    }

    Properties jsonPropertiesToProperties(JsonNode propertiesNode) {
        Properties props = new Properties();
        for (Iterator<Map.Entry<String, JsonNode>> it = propertiesNode.fields(); it.hasNext();) {
            Map.Entry<String, JsonNode> entry = it.next();
            JsonNode value = entry.getValue();
            if (!value.isTextual()) {
                throw new IllegalArgumentException("Only string values supported: " + value);
            }
            props.setProperty(entry.getKey(), value.textValue());
        }
        return props;
    }
    
}
