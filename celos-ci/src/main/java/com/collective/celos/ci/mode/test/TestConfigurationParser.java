package com.collective.celos.ci.mode.test;

import com.collective.celos.JSConfigParser;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.mozilla.javascript.Context;
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

    public void evaluateTestConfig(File testCaseFile) throws IOException {
        evaluateTestConfig(new FileReader(testCaseFile), testCaseFile.getName());
    }

    Object evaluateTestConfig(Reader reader, String desc) throws IOException {
        Global scope = jsConfigParser.createGlobalScope();

        Object wrappedContext = Context.javaToJS(this, scope);
        Map jsProperties = Maps.newHashMap();
        jsProperties.put("testConfigurationParser", wrappedContext);
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
