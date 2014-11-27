package com.collective.celos.ci.mode.test;

import com.collective.celos.JSConfigParser;
import com.collective.celos.ci.CelosCi;
import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by akonopko on 21.11.14.
 */
public class TestConfigurationParser {

    private final List<TestCase> testCases = Lists.newArrayList();
    private final JSConfigParser jsConfigParser = new JSConfigParser();

    public void evaluateTestConfig(CelosCiCommandLine commandLine, File testCaseFile) throws IOException {
        evaluateTestConfig(commandLine, new FileReader(testCaseFile), testCaseFile.getName());
    }

    Object evaluateTestConfig(CelosCiCommandLine commandLine, Reader reader, String desc) throws IOException {
        Global scope = jsConfigParser.createGlobalScope();

        Object wrappedContext = Context.javaToJS(this, scope);
        Object wrappedCommandLine = Context.javaToJS(commandLine, scope);
        Map jsProperties = Maps.newHashMap();
        jsProperties.put("testConfigurationParser", wrappedContext);
        jsProperties.put("commandLine", wrappedCommandLine);
        jsConfigParser.putPropertiesInScope(jsProperties, scope);

        InputStream scripts = getClass().getResourceAsStream("celos-ci-scripts.js");
        jsConfigParser.evaluateReader(scope, new InputStreamReader(scripts), "celos-ci-scripts.js");
        return jsConfigParser.evaluateReader(scope, reader, desc);
    }

    public void addTestCase(TestCase testCase) {
        testCases.add(testCase);
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

}
