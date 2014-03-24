package com.collective.celos;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;

import com.collective.celos.api.Schedule;
import com.collective.celos.api.ScheduledTime;
import com.collective.celos.api.Trigger;
import com.collective.celos.api.Util;
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
    
    public static final String EXTERNAL_SERVICE_PROP = "externalService";
    public static final String TRIGGER_PROP = "trigger";
    public static final String SCHEDULING_STRATEGY_PROP = "schedulingStrategy";
    public static final String SCHEDULE_PROP = "schedule";
    public static final String ID_PROP = "id";
    public static final String MAX_RETRY_COUNT_PROP = "maxRetryCount";
    public static final String START_TIME_PROP = "startTime";

    private static final Logger LOGGER = Logger.getLogger(WorkflowConfigurationParser.class);
    
    private final JSONInstanceCreator creator = new JSONInstanceCreator();
    private final ObjectMapper mapper = new ObjectMapper();
    private final WorkflowConfiguration cfg = new WorkflowConfiguration();
    private final File defaultsDir;
    private final Context context;
    
    public WorkflowConfigurationParser(File defaultsDir) throws Exception {
        this.defaultsDir = Util.requireNonNull(defaultsDir);
        context = Context.enter();
        context.setLanguageVersion(170);
    }

    public WorkflowConfigurationParser parseConfiguration(File workflowsDir) {
        LOGGER.info("Using workflows directory: " + workflowsDir);
        LOGGER.info("Using defaults directory: " + defaultsDir);
        Collection<File> files = FileUtils.listFiles(workflowsDir, new String[] { WORKFLOW_FILE_EXTENSION }, false);
        for (File f : files) {
            try {
                parseFile(f);
            } catch(Exception e) {
                LOGGER.error("Failed to load file: " + f + ": " + e.getMessage(), e);
            }
        }
        return this;
    }

    void parseFile(File f) throws Exception {
        LOGGER.info("Loading file: " + f);
        FileReader fileReader = new FileReader(f);
        String fileName = f.toString();
        int lineNo = 1;
        evaluateReader(fileReader, fileName, lineNo);
    }

    Object evaluateReader(Reader r, String fileName, int lineNo) throws Exception, IOException {
        Global scope = new Global();
        scope.initStandardObjects(context, true);
        setupBindings(scope);
        loadBuiltinScripts(scope);
        return context.evaluateReader(scope, r, fileName, lineNo, null);
    }
    
    private void setupBindings(Global scope) {
        Object wrappedThis = Context.javaToJS(this, scope);
        ScriptableObject.putProperty(scope, "celosWorkflowConfigurationParser", wrappedThis);
        // Need to put scope into JS so it can call importDefaultsIntoScope
        Object wrappedScope = Context.javaToJS(scope, scope);
        ScriptableObject.putProperty(scope, "celosScope", wrappedScope);
    }

    private void loadBuiltinScripts(Global scope) throws Exception {
        InputStream scripts = WorkflowConfigurationParser.class.getResourceAsStream("celos-scripts.js");
        context.evaluateReader(scope, new InputStreamReader(scripts), "celos-scripts.js", 1, null);
    }
    
    public WorkflowConfiguration getWorkflowConfiguration() {
        return cfg;
    }
    
    @SuppressWarnings("unused") // this is used from JS only
    public void importDefaultsIntoScope(String label, Global scope) throws IOException {
        File defaultsFile = new File(defaultsDir, label + "." + WORKFLOW_FILE_EXTENSION);
        LOGGER.info("Loading defaults: " + defaultsFile);
        FileReader fileReader = new FileReader(defaultsFile);
        String fileName = defaultsFile.toString();
        int lineNo = 1;
        context.evaluateReader(scope, fileReader, fileName, lineNo, null);
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

    ScheduledTime getStartTimeFromJSON(JsonNode workflowNode) {
        JsonNode startTimeNode = workflowNode.get(START_TIME_PROP);
        if (startTimeNode == null) {
            return Workflow.DEFAULT_START_TIME;
        } else if (!startTimeNode.isTextual()) {
            throw new IllegalArgumentException("startTime must be a string: " + workflowNode.toString());
        } else {
            return new ScheduledTime(startTimeNode.textValue());
        }
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

    public Context getContext() {
        return context;
    }

}
