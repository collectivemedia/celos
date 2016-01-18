/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

import com.collective.celos.database.StateDatabaseConnection;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
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
 * The helper script celos-scripts.js defines the utility function celos.defineWorkflow(object)
 * Reads a set of JS files from a directory and creates a WorkflowConfiguration.
 */
public class WorkflowConfigurationParser {

    public static final String WORKFLOW_FILE_EXTENSION = "js";

    private static final Logger LOGGER = Logger.getLogger(WorkflowConfigurationParser.class);
    private static final String CELOS_SCRIPTS_FILENAME = "celos-scripts.js";
    private static final String CELOS_SCRIPTS_EXC_PREFIX = CELOS_SCRIPTS_FILENAME + ": ";

    private final WorkflowConfiguration cfg = new WorkflowConfiguration();
    private final JSConfigParser jsConfigParser = new JSConfigParser();
    private final File defaultsDir;
    private final Map<String, String> additionalJsVariables;

    public WorkflowConfigurationParser(File defaultsDir, Map<String, String> additionalJsVariables) throws Exception {
        this.defaultsDir = Util.requireNonNull(defaultsDir);
        this.additionalJsVariables = additionalJsVariables;
    }

    public WorkflowConfigurationParser parseConfiguration(File workflowsDir, StateDatabaseConnection connection) {
        LOGGER.info("Using workflows directory: " + workflowsDir);
        LOGGER.info("Using defaults directory: " + defaultsDir);
        Collection<File> files = FileUtils.listFiles(workflowsDir, new String[] { WORKFLOW_FILE_EXTENSION }, false);
        for (File f : files) {
            try {
                parseFile(f, connection);
            } catch(Exception e) {
                LOGGER.error("Failed to load file: " + f + ": " + e.getMessage(), e);
            }
        }
        return this;
    }

    void parseFile(File f, StateDatabaseConnection connection) throws Exception {
        LOGGER.info("Loading file: " + f);
        FileReader fileReader = new FileReader(f);
        String fileName = f.toString();
        evaluateReader(fileReader, fileName, connection);
    }

    Object evaluateReader(Reader r, String fileName, StateDatabaseConnection connection) throws Exception {

        Global scope = jsConfigParser.createGlobalScope();

        Object wrappedThis = Context.javaToJS(this, scope);
        Map jsProperties = Maps.newHashMap(additionalJsVariables);
        jsProperties.put("celosWorkflowConfigurationParser", wrappedThis);
        jsProperties.put("celosConnection", connection);

        jsConfigParser.putPropertiesInScope(jsProperties, scope);

        InputStream scripts = WorkflowConfigurationParser.class.getResourceAsStream(CELOS_SCRIPTS_FILENAME);
        jsConfigParser.evaluateReader(scope, new InputStreamReader(scripts), fileName);

        try {
            return jsConfigParser.evaluateReader(scope, r, fileName);
        } catch (JavaScriptException e) {
            return rethrowException(e);
        }
    }

    private Object rethrowException(JavaScriptException e) {
        if (e.getValue() instanceof String) {
            String message = (String) e.getValue();
            if (message.startsWith(CELOS_SCRIPTS_EXC_PREFIX)) {
                throw new JavaScriptException(message.substring(CELOS_SCRIPTS_EXC_PREFIX.length()), CELOS_SCRIPTS_FILENAME, e.lineNumber());
            }
        }
        throw e;
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
