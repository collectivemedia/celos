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
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.tools.shell.Global;

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
    
    private final ObjectMapper mapper = new ObjectMapper();
    private final WorkflowConfiguration cfg = new WorkflowConfiguration();
    private final File defaultsDir;
    private final Context context;
    
    public WorkflowConfigurationParser(File defaultsDir) throws Exception {
        this.defaultsDir = Util.requireNonNull(defaultsDir);
        context = Context.enter();
        context.setLanguageVersion(170);

        /**
         * Treat primitives like strings returned from Java methods
         * as native JS objects.
         */
        WrapFactory wf = new WrapFactory();
        wf.setJavaPrimitiveWrap(false);
        context.setWrapFactory(wf);
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
        setupBindings(scope, fileName);
        loadBuiltinScripts(scope);
        return context.evaluateReader(scope, r, fileName, lineNo, null);
    }
    
    private void setupBindings(Global scope, String celosWorkflowConfigFilePath) {
        Object wrappedThis = Context.javaToJS(this, scope);
        ScriptableObject.putProperty(scope, "celosWorkflowConfigurationParser", wrappedThis);
        Object wrappedMapper = Context.javaToJS(mapper, scope);
        ScriptableObject.putProperty(scope, "celosMapper", wrappedMapper);
        // Need to put scope into JS so it can call importDefaultsIntoScope
        Object wrappedScope = Context.javaToJS(scope, scope);
        ScriptableObject.putProperty(scope, "celosScope", wrappedScope);
        ScriptableObject.putProperty(scope, "celosWorkflowConfigFilePath", celosWorkflowConfigFilePath);
    }

    private void loadBuiltinScripts(Global scope) throws Exception {
        InputStream scripts = WorkflowConfigurationParser.class.getResourceAsStream("celos-scripts.js");
        context.evaluateReader(scope, new InputStreamReader(scripts), "celos-scripts.js", 1, null);
    }
    
    public WorkflowConfiguration getWorkflowConfiguration() {
        return cfg;
    }
    
    public void importDefaultsIntoScope(String label, Global scope) throws IOException {
        File defaultsFile = new File(defaultsDir, label + "." + WORKFLOW_FILE_EXTENSION);
        LOGGER.info("Loading defaults: " + defaultsFile);
        FileReader fileReader = new FileReader(defaultsFile);
        String fileName = defaultsFile.toString();
        int lineNo = 1;
        context.evaluateReader(scope, fileReader, fileName, lineNo, null);
    }
    
    public void addWorkflow(Workflow wf, String celosWorkflowConfigFilePath) {
        cfg.addWorkflow(wf, celosWorkflowConfigFilePath);
    }
    
    public Context getContext() {
        return context;
    }

}
