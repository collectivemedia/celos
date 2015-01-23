package com.collective.celos.ci.mode.test;

import com.collective.celos.ScheduledTime;
import com.collective.celos.WorkflowID;
import com.collective.celos.ci.testing.fixtures.compare.RecursiveDirComparer;
import com.collective.celos.ci.testing.fixtures.compare.json.JsonContentsComparer;
import com.collective.celos.ci.testing.fixtures.convert.AvroToJsonConverter;
import com.collective.celos.ci.testing.fixtures.create.FixDirFromResourceCreator;
import com.collective.celos.ci.testing.fixtures.create.FixDirHierarchyCreator;
import com.collective.celos.ci.testing.fixtures.create.FixFileFromResourceCreator;
import com.collective.celos.ci.testing.fixtures.create.OutputFixDirFromHdfsCreator;
import com.collective.celos.ci.testing.fixtures.deploy.HdfsInputDeployer;
import com.collective.celos.ci.testing.fixtures.deploy.hive.HiveFileCreator;
import com.collective.celos.ci.testing.fixtures.deploy.hive.HiveTableDeployer;
import com.collective.celos.ci.testing.structure.fixobject.ConvertionCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixDirRecursiveConverter;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 27.11.14.
 */
public class TestConfigurationParserTest {

    private TestRun testRun;

    @Before
    public void setUp() {
        testRun = mock(TestRun.class);
        doReturn(new File("/")).when(testRun).getTestCasesDir();
    }

    @Test
    public void testConfigurationParserWorks() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();
        String filePath = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/defaults/test.js").getFile();
        parser.evaluateTestConfig(new File(filePath));
    }

    @Test
    public void fixDirFromResource() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("fixDirFromResource(\"stuff\")"), "string");
        FixDirFromResourceCreator creator = (FixDirFromResourceCreator) creatorObj.unwrap();
        Assert.assertEquals(new File("/stuff"), creator.getPath(testRun));
    }

    @Test(expected = JavaScriptException.class)
    public void fixDirFromResourceFails() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("fixDirFromResource()"), "string");
        FixDirFromResourceCreator creator = (FixDirFromResourceCreator) creatorObj.unwrap();
        Assert.assertEquals(new File("/stuff"), creator.getPath(testRun));
    }


    @Test
    public void fixFileFromResource() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("fixFileFromResource(\"stuff\")"), "string");
        FixFileFromResourceCreator creator = (FixFileFromResourceCreator) creatorObj.unwrap();
        Assert.assertEquals(new File("/stuff"), creator.getPath(testRun));
    }

    @Test(expected = JavaScriptException.class)
    public void fixFileFromResourceFails() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("fixFileFromResource()"), "string");
        FixFileFromResourceCreator creator = (FixFileFromResourceCreator) creatorObj.unwrap();
        Assert.assertEquals(new File("stuff"), creator.getPath(testRun));
    }

    @Test(expected = JavaScriptException.class)
    public void testHdfsInputDeployerCall1() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("hdfsInput()"), "string");
        HdfsInputDeployer creator = (HdfsInputDeployer) creatorObj.unwrap();
        Assert.assertEquals(new File("stuff"), creator.getPath());
    }

    @Test
    public void testHdfsInputDeployerCall2() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("hdfsInput(fixFileFromResource(\"stuff\"), \"here\")"), "string");
        HdfsInputDeployer creator = (HdfsInputDeployer) creatorObj.unwrap();
        Assert.assertEquals(new Path("here"), creator.getPath());
    }

    @Test(expected = JavaScriptException.class)
    public void testRecursiveDirComparer1() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("plainCompare()"), "string");
        RecursiveDirComparer creator = (RecursiveDirComparer) creatorObj.unwrap();
    }

    @Test
    public void testRecursiveDirComparer2() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("plainCompare(fixDirFromResource(\"stuff\"), \"here\")"), "string");

        RecursiveDirComparer comparer = (RecursiveDirComparer) creatorObj.unwrap();

        OutputFixDirFromHdfsCreator actualCreator = (OutputFixDirFromHdfsCreator) comparer.getActualDataCreator();
        FixDirFromResourceCreator expectedDataCreator = (FixDirFromResourceCreator) comparer.getExpectedDataCreator();
        Assert.assertEquals(new Path("here"), actualCreator.getPath());
        Assert.assertEquals(new File("/stuff"), expectedDataCreator.getPath(testRun));
    }

    @Test(expected = JavaScriptException.class)
    public void testRecursiveDirComparer3() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("plainCompare(fixFileFromResource(\"stuff\"))"), "string");
        RecursiveDirComparer creator = (RecursiveDirComparer) creatorObj.unwrap();
    }


    @Test
    public void testAddTestCase() throws IOException {

        String configJS = "addTestCase({\n" +
                "    name: \"wordcount test case 1\",\n" +
                "    sampleTimeStart: \"2013-11-20T11:00Z\",\n" +
                "    sampleTimeEnd: \"2013-11-20T18:00Z\",\n" +
                "    workflows: [\"workflow1\", \"workflow2\"], \n" +
                "    inputs: [\n" +
                "        hdfsInput(fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount1\"), \"input/wordcount1\"),\n" +
                "        hdfsInput(fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount11\"), \"input/wordcount11\")\n" +
                "    ],\n" +
                "    outputs: [\n" +
                "        plainCompare(fixDirFromResource(\"src/test/celos-ci/test-1/output/plain/output/wordcount1\"), \"output/wordcount1\")\n" +
                "    ]\n" +
                "})\n";

        TestConfigurationParser parser = new TestConfigurationParser();
        parser.evaluateTestConfig(new StringReader(configJS), "string");

        Assert.assertEquals(parser.getTestCases().size(), 1);
        TestCase testCase = parser.getTestCases().get(0);

        Assert.assertEquals(testCase.getName(), "wordcount test case 1");
        Assert.assertEquals(testCase.getSampleTimeStart(), new ScheduledTime("2013-11-20T11:00Z"));
        Assert.assertEquals(testCase.getSampleTimeEnd(), new ScheduledTime("2013-11-20T18:00Z"));
        Assert.assertEquals(testCase.getTargetWorkflows(), Sets.newHashSet(new WorkflowID("workflow1"), new WorkflowID("workflow2")));
        Assert.assertEquals(testCase.getInputs().size(), 2);
        Assert.assertEquals(testCase.getInputs().get(0).getClass(), HdfsInputDeployer.class);
        Assert.assertEquals(testCase.getInputs().get(1).getClass(), HdfsInputDeployer.class);
        Assert.assertEquals(testCase.getOutputs().size(), 1);
        Assert.assertEquals(testCase.getOutputs().get(0).getClass(), RecursiveDirComparer.class);
    }

    @Test(expected = JavaScriptException.class)
    public void testAddTestCaseNoOutput() throws IOException {

        String configJS = "addTestCase({\n" +
                "    name: \"wordcount test case 1\",\n" +
                "    sampleTimeStart: \"2013-11-20T11:00Z\",\n" +
                "    sampleTimeEnd: \"2013-11-20T18:00Z\",\n" +
                "    inputs: [\n" +
                "        hdfsInput(fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount1\"), \"input/wordcount1\"),\n" +
                "        hdfsInput(fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount11\"), \"input/wordcount11\")\n" +
                "    ]\n" +
                "})\n";

        TestConfigurationParser parser = new TestConfigurationParser();

        parser.evaluateTestConfig(new StringReader(configJS), "string");

    }


    @Test(expected = JavaScriptException.class)
    public void testAddTestCaseNoInput() throws IOException {

        String configJS = "addTestCase({\n" +
                "    name: \"wordcount test case 1\",\n" +
                "    sampleTimeStart: \"2013-11-20T11:00Z\",\n" +
                "    sampleTimeEnd: \"2013-11-20T18:00Z\",\n" +
                "    outputs: [\n" +
                "        plainCompare(fixDirFromResource(\"src/test/celos-ci/test-1/output/plain/output/wordcount1\"), \"output/wordcount1\")\n" +
                "    ]\n" +
                "})\n";

        TestConfigurationParser parser = new TestConfigurationParser();

        parser.evaluateTestConfig(new StringReader(configJS), "string");

    }


    @Test(expected = JavaScriptException.class)
    public void testAddTestCaseNoSampleTimeStart() throws IOException {

        String configJS = "addTestCase({\n" +
                "    name: \"wordcount test case 1\",\n" +
                "    sampleTimeEnd: \"2013-11-20T18:00Z\",\n" +
                "    inputs: [\n" +
                "        hdfsInput(fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount1\"), \"input/wordcount1\"),\n" +
                "        hdfsInput(fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount11\"), \"input/wordcount11\")\n" +
                "    ],\n" +
                "    outputs: [\n" +
                "        plainCompare(fixDirFromResource(\"src/test/celos-ci/test-1/output/plain/output/wordcount1\"), \"output/wordcount1\")\n" +
                "    ]\n" +
                "})\n";

        TestConfigurationParser parser = new TestConfigurationParser();

        parser.evaluateTestConfig(new StringReader(configJS), "string");
    }

    @Test(expected = JavaScriptException.class)
    public void testAddTestCaseNoSampleTimeEnd() throws IOException {

        String configJS = "addTestCase({\n" +
                "    name: \"wordcount test case 1\",\n" +
                "    sampleTimeStart: \"2013-11-20T18:00Z\",\n" +
                "    inputs: [\n" +
                "        hdfsInput(fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount1\"), \"input/wordcount1\"),\n" +
                "        hdfsInput(fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount11\"), \"input/wordcount11\")\n" +
                "    ],\n" +
                "    outputs: [\n" +
                "        plainCompare(fixDirFromResource(\"src/test/celos-ci/test-1/output/plain/output/wordcount1\"), \"output/wordcount1\")\n" +
                "    ]\n" +
                "})\n";

        TestConfigurationParser parser = new TestConfigurationParser();

        parser.evaluateTestConfig(new StringReader(configJS), "string");
    }

    @Test(expected = JavaScriptException.class)
    public void testAvroToJsonFails() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        parser.evaluateTestConfig(new StringReader("avroToJson()"), "string");
    }

    @Test
    public void testAvroToJson() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("avroToJson(\"1\")"), "string");

        ConvertionCreator converter = (ConvertionCreator) creatorObj.unwrap();

        Assert.assertTrue(converter.getFixObjectConverter() instanceof FixDirRecursiveConverter);
        FixDirRecursiveConverter fixDirRecursiveConverter = (FixDirRecursiveConverter) converter.getFixObjectConverter();
        Assert.assertTrue(fixDirRecursiveConverter.getFixFileConverter() instanceof AvroToJsonConverter);
        Assert.assertTrue(converter.getCreator() instanceof OutputFixDirFromHdfsCreator);
    }

    @Test
    public void testJsonCompare() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("jsonCompare(fixDirFromResource(\"stuff\"), fixDirFromResource(\"stuff\"))"), "string");

        JsonContentsComparer comparer = (JsonContentsComparer) creatorObj.unwrap();
        Assert.assertEquals(comparer.getIgnorePaths(), new HashSet<String>());
    }

    @Test
    public void testJsonCompare2() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("jsonCompare(fixDirFromResource(\"stuff\"), fixDirFromResource(\"stuff\"), [\"path1\", \"path2\"])"), "string");

        JsonContentsComparer comparer = (JsonContentsComparer) creatorObj.unwrap();
        Assert.assertEquals(comparer.getIgnorePaths(), new HashSet(Lists.newArrayList("path1", "path2")));
    }


    @Test(expected = JavaScriptException.class)
    public void testJsonCompareFails() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("jsonCompare(fixDirFromResource(\"stuff\"))"), "string");

        JsonContentsComparer comparer = (JsonContentsComparer) creatorObj.unwrap();
        Assert.assertEquals(comparer.getIgnorePaths(), new HashSet(Lists.newArrayList("path1", "path2")));
    }

    @Test
    public void testFixDir() throws Exception {
        String js = "" +
                "fixDir({" +
                "   file1: fixFile('123')," +
                "   file2: fixFile('234')" +
                "})";

        TestConfigurationParser parser = new TestConfigurationParser();
        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        FixDirHierarchyCreator creator = (FixDirHierarchyCreator) creatorObj.unwrap();
        FixDir fixDir = creator.create(null);

        Assert.assertEquals(fixDir.getChildren().size(), 2);
        Assert.assertTrue(IOUtils.contentEquals(fixDir.getChildren().get("file1").asFile().getContent(), new ByteArrayInputStream("123".getBytes())));
        Assert.assertTrue(IOUtils.contentEquals(fixDir.getChildren().get("file2").asFile().getContent(), new ByteArrayInputStream("234".getBytes())));
    }


    @Test
    public void testFixDirWithFixDir() throws Exception {
        String js = "" +
                "fixDir({" +
                "    file0: fixFile('012')," +
                "    dir1: fixDir({" +
                "        file1: fixFile('123')," +
                "        file2: fixFile('234')" +
                "    })" +
                "})";

        TestConfigurationParser parser = new TestConfigurationParser();
        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        FixDirHierarchyCreator creator = (FixDirHierarchyCreator) creatorObj.unwrap();
        FixDir fixDir = creator.create(null);

        Assert.assertEquals(fixDir.getChildren().size(), 2);
        Assert.assertTrue(IOUtils.contentEquals(fixDir.getChildren().get("file0").asFile().getContent(), new ByteArrayInputStream("012".getBytes())));

        FixDir fixDir2 = (FixDir) fixDir.getChildren().get("dir1");
        Assert.assertEquals(fixDir2.getChildren().size(), 2);
        Assert.assertTrue(IOUtils.contentEquals(fixDir2.getChildren().get("file1").asFile().getContent(), new ByteArrayInputStream("123".getBytes())));
        Assert.assertTrue(IOUtils.contentEquals(fixDir2.getChildren().get("file2").asFile().getContent(), new ByteArrayInputStream("234".getBytes())));

    }

    @Test
    public void testJsonToAvro() throws Exception {

        String jsonStr = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"133263e9e100000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"REMOVE\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}},{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49117]}}}]}";
        String schemaStr = "{\"type\":\"record\",\"name\":\"ConsolidatedEvent\",\"namespace\":\"com.collective.pythia.avro\",\"fields\":[{\"name\":\"visitor\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"Visitor\",\"fields\":[{\"name\":\"cookie_id\",\"type\":\"string\"},{\"name\":\"segments\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"Segment\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"expiration\",\"type\":\"long\",\"default\":0}]}},\"default\":[]},{\"name\":\"edges\",\"type\":{\"type\":\"map\",\"values\":\"long\"},\"default\":{}},{\"name\":\"behaviors\",\"type\":{\"type\":\"map\",\"values\":{\"type\":\"map\",\"values\":\"int\"},\"default\":{}},\"doc\":\"Map of net.context to map of YYYYMMDD->number_of_hits\",\"default\":{}},{\"name\":\"birthdate\",\"type\":\"long\"},{\"name\":\"association_ids\",\"type\":{\"type\":\"map\",\"values\":\"string\"},\"default\":{}}]}],\"default\":null},{\"name\":\"events\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"ProfileEvent\",\"fields\":[{\"name\":\"cookie_id\",\"type\":\"string\"},{\"name\":\"tstamp\",\"type\":\"long\"},{\"name\":\"edge\",\"type\":\"string\"},{\"name\":\"changes\",\"type\":[{\"type\":\"record\",\"name\":\"Hit\",\"fields\":[{\"name\":\"daystamp\",\"type\":\"string\"},{\"name\":\"context\",\"type\":\"string\"},{\"name\":\"type\",\"type\":{\"type\":\"enum\",\"name\":\"HitType\",\"symbols\":[\"ADX\",\"RETARGET\"]}},{\"name\":\"count\",\"type\":\"int\",\"default\":1}]},{\"type\":\"record\",\"name\":\"Command\",\"fields\":[{\"name\":\"operation\",\"type\":{\"type\":\"enum\",\"name\":\"OperationType\",\"symbols\":[\"ADD\",\"REPLACE\",\"UPDATE\",\"REMOVE\"]}},{\"name\":\"association_id\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"network\",\"type\":\"string\"},{\"name\":\"segments\",\"type\":{\"type\":\"array\",\"items\":\"int\"},\"default\":[]}]}]}]}},\"default\":[]}]}";

        String js = "" +
                "jsonToAvro(" +
                "    fixDir({ avroFile1: fixFile('" + jsonStr + "'), avroFile2: fixFile('" + jsonStr + "')})," +
                "    fixFile('" + schemaStr + "')" +
                ");";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        ConvertionCreator<FixDir, FixDir> converter = (ConvertionCreator) creatorObj.unwrap();

        Assert.assertEquals(converter.getDescription(null), "[avroFile1, avroFile2]");
        FixDir fixDir = converter.create(null);

        Assert.assertEquals(fixDir.getChildren().size(), 2);

        AvroToJsonConverter avroToJsonConverter = new AvroToJsonConverter();
        FixFile jsonFF = avroToJsonConverter.convert(null, new FixFile(fixDir.getChildren().get("avroFile1").asFile().getContent()));
        String jsonIsBack = IOUtils.toString(jsonFF.getContent());
        Assert.assertEquals(jsonIsBack, jsonStr);


        FixFile jsonFF2 = avroToJsonConverter.convert(null, new FixFile(fixDir.getChildren().get("avroFile2").asFile().getContent()));
        String jsonIsBack2 = IOUtils.toString(jsonFF2.getContent());
        Assert.assertEquals(jsonIsBack2, jsonStr);

    }

    @Test
    public void testHiveInput() throws IOException {

        String js = "hiveInput(\"dbname\", \"tablename\", [[\"1\",\"2\",\"3\"],[\"11\",\"22\",\"33\"]])";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        HiveTableDeployer hiveTableDeployer = (HiveTableDeployer) creatorObj.unwrap();

        Assert.assertEquals(hiveTableDeployer.getDatabaseName(), "dbname");
        Assert.assertEquals(hiveTableDeployer.getTableName(), "tablename");
        HiveFileCreator.ContentHiveFileCreator creator = (HiveFileCreator.ContentHiveFileCreator) hiveTableDeployer.getDataFileCreator();
        Assert.assertArrayEquals(creator.getCellData(), new String[][]{new String[]{"1", "2", "3"}, new String[]{"11", "22", "33"}});

    }

    @Test(expected = JavaScriptException.class)
    public void testHiveInputFails() throws IOException {

        String js = "hiveInput(\"tablename\", [[\"1\",\"2\",\"3\"],[\"11\",\"22\",\"33\"]])";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        HiveTableDeployer hiveTableDeployer = (HiveTableDeployer) creatorObj.unwrap();
    }

    @Test
    public void testHiveInputNoData() throws IOException {

        String js = "hiveInput(\"dbname\", \"tablename\")";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        HiveTableDeployer hiveTableDeployer = (HiveTableDeployer) creatorObj.unwrap();
    }

}
