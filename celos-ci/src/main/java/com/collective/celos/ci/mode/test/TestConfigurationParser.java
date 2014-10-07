package com.collective.celos.ci.mode.test;

import com.collective.celos.Workflow;
import com.collective.celos.WorkflowConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.tools.shell.Global;

import java.io.*;
import java.util.Map;

public class TestConfigurationParser {

    private static final Logger LOGGER = Logger.getLogger(TestConfigurationParser.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final WorkflowConfiguration cfg = new WorkflowConfiguration();
    private final Context context;

    public TestConfigurationParser() throws Exception {
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

    public void parseFile(File f) throws Exception {
        LOGGER.info("Loading file: " + f);
        FileReader fileReader = new FileReader(f);
        String fileName = f.toString();
        evaluateReader(fileReader, fileName);
    }

    public Object evaluateReader(Reader r, String fileName) throws Exception, IOException {
        Global scope = new Global();
        scope.initStandardObjects(context, true);
        setupBindings(scope);
        loadBuiltinScripts(scope);

        return context.evaluateReader(scope, r, fileName, 1, null);
    }

    private void setupBindings(Global scope) {
        Object wrappedThis = Context.javaToJS(this, scope);
        ScriptableObject.putProperty(scope, "celosWorkflowConfigurationParser", wrappedThis);
        Object wrappedMapper = Context.javaToJS(mapper, scope);
        ScriptableObject.putProperty(scope, "celosMapper", wrappedMapper);
        // Need to put scope into JS so it can call importDefaultsIntoScope
        Object wrappedScope = Context.javaToJS(scope, scope);
        ScriptableObject.putProperty(scope, "celosScope", wrappedScope);
    }

    private void loadBuiltinScripts(Global scope) throws Exception {
        InputStream scripts = TestConfigurationParser.class.getResourceAsStream("test-scripts.js");
        context.evaluateReader(scope, new InputStreamReader(scripts), "test-scripts.js", 1, null);
    }

    public Context getContext() {
        return context;
    }

}
