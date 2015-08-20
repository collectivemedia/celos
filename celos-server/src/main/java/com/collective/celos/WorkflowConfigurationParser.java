package com.collective.celos;

import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.tools.shell.Global;

import java.io.*;
import java.util.Collection;
import java.util.Map;

/**
 * Reads a set of JS files from a directory and creates a WorkflowConfiguration.
 * 
 * The JS engine has one variable defined, celosWorkflowConfigurationParser, which
 * points to the parser instance.
 * 
 * The helper script celos-scripts.js defines the utility function celos.addWorkflow(object),
 * which stringifies the input JS object, and passes the string to the parser's
 * celos.addWorkflowFromJSONString method.
 */
public class WorkflowConfigurationParser {

    public static final String WORKFLOW_FILE_EXTENSION = "js";

    private static final Logger LOGGER = Logger.getLogger(WorkflowConfigurationParser.class);

    private final WorkflowConfiguration cfg = new WorkflowConfiguration();
    private final JSConfigParser jsConfigParser = new JSConfigParser();
    private final File defaultsDir;
    private final Map<String, String> additionalJsVariables;

    public WorkflowConfigurationParser(File defaultsDir, Map<String, String> additionalJsVariables) throws Exception {
        this.defaultsDir = Util.requireNonNull(defaultsDir);
        this.additionalJsVariables = additionalJsVariables;
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
        evaluateReader(fileReader, fileName);
    }

    Object evaluateReader(Reader r, String fileName) throws Exception {

        Global scope = jsConfigParser.createGlobalScope();

        Object wrappedThis = Context.javaToJS(this, scope);
        Map jsProperties = Maps.newHashMap(additionalJsVariables);
        jsProperties.put("celosWorkflowConfigurationParser", wrappedThis);

        jsConfigParser.putPropertiesInScope(jsProperties, scope);

        InputStream scripts = WorkflowConfigurationParser.class.getResourceAsStream("celos-scripts.js");
        jsConfigParser.evaluateReader(scope, new InputStreamReader(scripts), fileName);

        return jsConfigParser.evaluateReader(scope, r, fileName);
    }

    public WorkflowConfiguration getWorkflowConfiguration() {
        return cfg;
    }

    public void importDefaultsIntoScope(String label, Global scope) throws IOException {
        File defaultsFile = new File(defaultsDir, label + "." + WORKFLOW_FILE_EXTENSION);
        LOGGER.info("Loading defaults: " + defaultsFile);
        FileReader fileReader = new FileReader(defaultsFile);
        String fileName = defaultsFile.toString();
        jsConfigParser.evaluateReader(scope, fileReader, fileName);
    }

    public void addWorkflow(Workflow wf) {
        cfg.addWorkflow(wf);
    }

}
