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
//    private final CelosCiContext context;
    private final JSConfigParser jsConfigParser = new JSConfigParser();
//
//    public TestConfigurationParser(CelosCiContext context) {
//        this.context = context;
//    }

    public void evaluateTestConfig(File testCaseFile) throws IOException {
        Global scope = jsConfigParser.createGlobalScope();

        Object wrappedContext = Context.javaToJS(this, scope);
        Map jsProperties = Maps.newHashMap();
        jsProperties.put("testConfigurationParser", wrappedContext);
        jsConfigParser.putPropertiesInScope(jsProperties, scope);

        InputStream scripts = TestConfigurationParser.class.getResourceAsStream("celos-ci-scripts.js");
        jsConfigParser.evaluateReader(scope, new InputStreamReader(scripts), "celos-ci-scripts.js");
        jsConfigParser.evaluateReader(scope, new FileReader(testCaseFile), testCaseFile.getName());
    }

    public void addTestCase(TestCase testCase) {
        testCases.add(testCase);
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public static void main(String... args) throws IOException {
        File f = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/testing/config/test.js").getFile());
        TestConfigurationParser parser = new TestConfigurationParser();
        parser.evaluateTestConfig(f);
    }


}
