package com.collective.celos.ci.mode.test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaObject;

import com.collective.celos.DatabaseName;
import com.collective.celos.ScheduledTime;
import com.collective.celos.WorkflowID;
import com.collective.celos.ci.testing.fixtures.compare.FixTableComparer;
import com.collective.celos.ci.testing.fixtures.compare.RecursiveFsObjectComparer;
import com.collective.celos.ci.testing.fixtures.compare.json.JsonContentsComparer;
import com.collective.celos.ci.testing.fixtures.convert.AvroToJsonConverter;
import com.collective.celos.ci.testing.fixtures.convert.FixTableToJsonFileConverter;
import com.collective.celos.ci.testing.fixtures.convert.FixTableToTSVFileConverter;
import com.collective.celos.ci.testing.fixtures.convert.JsonExpandConverter;
import com.collective.celos.ci.testing.fixtures.create.FixDirFromResourceCreator;
import com.collective.celos.ci.testing.fixtures.create.FixDirHierarchyCreator;
import com.collective.celos.ci.testing.fixtures.create.FixFileFromResourceCreator;
import com.collective.celos.ci.testing.fixtures.create.OutputFixDirFromHdfsCreator;
import com.collective.celos.ci.testing.fixtures.create.OutputFixTableFromHiveCreator;
import com.collective.celos.ci.testing.fixtures.deploy.HdfsInputDeployer;
import com.collective.celos.ci.testing.fixtures.deploy.hive.FileFixTableCreator;
import com.collective.celos.ci.testing.fixtures.deploy.hive.HiveTableDeployer;
import com.collective.celos.ci.testing.fixtures.deploy.hive.StringArrayFixTableCreator;
import com.collective.celos.ci.testing.structure.fixobject.ConversionCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixDirRecursiveConverter;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("ci.fixDirFromResource(\"stuff\")"), "string");
        FixDirFromResourceCreator creator = (FixDirFromResourceCreator) creatorObj.unwrap();
        Assert.assertEquals(new File("/stuff"), creator.getPath(testRun));
    }

    @Test(expected = JavaScriptException.class)
    public void fixDirFromResourceFails() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("ci.fixDirFromResource()"), "string");
        FixDirFromResourceCreator creator = (FixDirFromResourceCreator) creatorObj.unwrap();
        Assert.assertEquals(new File("/stuff"), creator.getPath(testRun));
    }


    @Test
    public void fixFileFromResource() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("ci.fixFileFromResource(\"stuff\")"), "string");
        FixFileFromResourceCreator creator = (FixFileFromResourceCreator) creatorObj.unwrap();
        Assert.assertEquals(new File("/stuff"), creator.getPath(testRun));
    }

    @Test(expected = JavaScriptException.class)
    public void fixFileFromResourceFails() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("ci.fixFileFromResource()"), "string");
        FixFileFromResourceCreator creator = (FixFileFromResourceCreator) creatorObj.unwrap();
        Assert.assertEquals(new File("stuff"), creator.getPath(testRun));
    }

    @Test(expected = JavaScriptException.class)
    public void testHdfsInputDeployerCall1() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("ci.hdfsInput()"), "string");
        HdfsInputDeployer creator = (HdfsInputDeployer) creatorObj.unwrap();
        Assert.assertEquals(new File("stuff"), creator.getPath());
    }

    @Test
    public void testHdfsInputDeployerCall2() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("ci.hdfsInput(ci.fixFileFromResource(\"stuff\"), \"here\")"), "string");
        HdfsInputDeployer creator = (HdfsInputDeployer) creatorObj.unwrap();
        Assert.assertEquals(new Path("here"), creator.getPath());
    }

    @Test(expected = JavaScriptException.class)
    public void testRecursiveFsObjectComparer1() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("ci.plainCompare()"), "string");
        RecursiveFsObjectComparer creator = (RecursiveFsObjectComparer) creatorObj.unwrap();
    }

    @Test
    public void testRecursiveFsObjectComparer2() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("ci.plainCompare(ci.fixDirFromResource(\"stuff\"), \"here\")"), "string");

        RecursiveFsObjectComparer comparer = (RecursiveFsObjectComparer) creatorObj.unwrap();

        OutputFixDirFromHdfsCreator actualCreator = (OutputFixDirFromHdfsCreator) comparer.getActualDataCreator();
        FixDirFromResourceCreator expectedDataCreator = (FixDirFromResourceCreator) comparer.getExpectedDataCreator();
        Assert.assertEquals(new Path("here"), actualCreator.getPath());
        Assert.assertEquals(new File("/stuff"), expectedDataCreator.getPath(testRun));
    }

    @Test(expected = JavaScriptException.class)
    public void testRecursiveFsObjectComparer3() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("ci.plainCompare(ci.fixFileFromResource(\"stuff\"))"), "string");
        RecursiveFsObjectComparer creator = (RecursiveFsObjectComparer) creatorObj.unwrap();
    }


    @Test
    public void testAddTestCase() throws IOException {

        String configJS = "ci.addTestCase({\n" +
                "    name: \"wordcount test case 1\",\n" +
                "    sampleTimeStart: \"2013-11-20T11:00Z\",\n" +
                "    sampleTimeEnd: \"2013-11-20T18:00Z\",\n" +
                "    workflows: [\"workflow1\", \"workflow2\"], \n" +
                "    inputs: [\n" +
                "        ci.hdfsInput(ci.fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount1\"), \"input/wordcount1\"),\n" +
                "        ci.hdfsInput(ci.fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount11\"), \"input/wordcount11\")\n" +
                "    ],\n" +
                "    outputs: [\n" +
                "        ci.plainCompare(ci.fixDirFromResource(\"src/test/celos-ci/test-1/output/plain/output/wordcount1\"), \"output/wordcount1\")\n" +
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
        Assert.assertEquals(testCase.getOutputs().get(0).getClass(), RecursiveFsObjectComparer.class);
    }

    @Test(expected = JavaScriptException.class)
    public void testAddTestCaseNoOutput() throws IOException {

        String configJS = "ci.addTestCase({\n" +
                "    name: \"wordcount test case 1\",\n" +
                "    sampleTimeStart: \"2013-11-20T11:00Z\",\n" +
                "    sampleTimeEnd: \"2013-11-20T18:00Z\",\n" +
                "    inputs: [\n" +
                "        ci.hdfsInput(ci.fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount1\"), \"input/wordcount1\"),\n" +
                "        ci.hdfsInput(ci.fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount11\"), \"input/wordcount11\")\n" +
                "    ]\n" +
                "})\n";

        TestConfigurationParser parser = new TestConfigurationParser();

        parser.evaluateTestConfig(new StringReader(configJS), "string");

    }


    @Test(expected = JavaScriptException.class)
    public void testAddTestCaseNoInput() throws IOException {

        String configJS = "ci.addTestCase({\n" +
                "    name: \"wordcount test case 1\",\n" +
                "    sampleTimeStart: \"2013-11-20T11:00Z\",\n" +
                "    sampleTimeEnd: \"2013-11-20T18:00Z\",\n" +
                "    outputs: [\n" +
                "        ci.plainCompare(ci.fixDirFromResource(\"src/test/celos-ci/test-1/output/plain/output/wordcount1\"), \"output/wordcount1\")\n" +
                "    ]\n" +
                "})\n";

        TestConfigurationParser parser = new TestConfigurationParser();

        parser.evaluateTestConfig(new StringReader(configJS), "string");

    }


    @Test(expected = JavaScriptException.class)
    public void testAddTestCaseNoSampleTimeStart() throws IOException {

        String configJS = "ci.addTestCase({\n" +
                "    name: \"wordcount test case 1\",\n" +
                "    sampleTimeEnd: \"2013-11-20T18:00Z\",\n" +
                "    inputs: [\n" +
                "        ci.hdfsInput(ci.fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount1\"), \"input/wordcount1\"),\n" +
                "        ci.hdfsInput(ci.fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount11\"), \"input/wordcount11\")\n" +
                "    ],\n" +
                "    outputs: [\n" +
                "        ci.plainCompare(ci.fixDirFromResource(\"src/test/celos-ci/test-1/output/plain/output/wordcount1\"), \"output/wordcount1\")\n" +
                "    ]\n" +
                "})\n";

        TestConfigurationParser parser = new TestConfigurationParser();

        parser.evaluateTestConfig(new StringReader(configJS), "string");
    }

    @Test(expected = JavaScriptException.class)
    public void testAddTestCaseNoSampleTimeEnd() throws IOException {

        String configJS = "ci.addTestCase({\n" +
                "    name: \"wordcount test case 1\",\n" +
                "    sampleTimeStart: \"2013-11-20T18:00Z\",\n" +
                "    inputs: [\n" +
                "        ci.hdfsInput(ci.fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount1\"), \"input/wordcount1\"),\n" +
                "        ci.hdfsInput(ci.fixDirFromResource(\"src/test/celos-ci/test-1/input/plain/input/wordcount11\"), \"input/wordcount11\")\n" +
                "    ],\n" +
                "    outputs: [\n" +
                "        ci.plainCompare(ci.fixDirFromResource(\"src/test/celos-ci/test-1/output/plain/output/wordcount1\"), \"output/wordcount1\")\n" +
                "    ]\n" +
                "})\n";

        TestConfigurationParser parser = new TestConfigurationParser();

        parser.evaluateTestConfig(new StringReader(configJS), "string");
    }

    @Test(expected = JavaScriptException.class)
    public void testAvroToJsonFails() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        parser.evaluateTestConfig(new StringReader("ci.avroToJson()"), "string");
    }

    @Test
    public void testAvroToJson() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("ci.avroToJson(\"1\")"), "string");

        ConversionCreator converter = (ConversionCreator) creatorObj.unwrap();

        Assert.assertTrue(converter.getFixObjectConverter() instanceof FixDirRecursiveConverter);
        FixDirRecursiveConverter fixDirRecursiveConverter = (FixDirRecursiveConverter) converter.getFixObjectConverter();
        Assert.assertTrue(fixDirRecursiveConverter.getFixFileConverter() instanceof AvroToJsonConverter);
        Assert.assertTrue(converter.getCreator() instanceof OutputFixDirFromHdfsCreator);
    }

    @Test
    public void testJsonCompare() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("ci.jsonCompare(ci.fixDirFromResource(\"stuff\"), ci.fixDirFromResource(\"stuff\"))"), "string");

        JsonContentsComparer comparer = (JsonContentsComparer) creatorObj.unwrap();
        Assert.assertEquals(comparer.getIgnorePaths(), new HashSet<String>());
    }

    @Test
    public void testJsonCompare2() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("ci.jsonCompare(ci.fixDirFromResource(\"stuff\"), ci.fixDirFromResource(\"stuff\"), [\"path1\", \"path2\"])"), "string");

        JsonContentsComparer comparer = (JsonContentsComparer) creatorObj.unwrap();
        Assert.assertEquals(comparer.getIgnorePaths(), new HashSet(Lists.newArrayList("path1", "path2")));
    }


    @Test(expected = JavaScriptException.class)
    public void testJsonCompareFails() throws IOException {
        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader("ci.jsonCompare(ci.fixDirFromResource(\"stuff\"))"), "string");

        JsonContentsComparer comparer = (JsonContentsComparer) creatorObj.unwrap();
        Assert.assertEquals(comparer.getIgnorePaths(), new HashSet(Lists.newArrayList("path1", "path2")));
    }

    @Test
    public void testFixDir() throws Exception {
        String js = "" +
                "ci.fixDir({" +
                "   file1: ci.fixFile('123')," +
                "   file2: ci.fixFile('234')" +
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
                "ci.fixDir({" +
                "    file0: ci.fixFile('012')," +
                "    dir1: ci.fixDir({" +
                "        file1: ci.fixFile('123')," +
                "        file2: ci.fixFile('234')" +
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
                "ci.jsonToAvro(" +
                "    ci.fixDir({ avroFile1: ci.fixFile('" + jsonStr + "'), avroFile2: ci.fixFile('" + jsonStr + "')})," +
                "    ci.fixFile('" + schemaStr + "')" +
                ");";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        ConversionCreator<FixDir, FixDir> converter = (ConversionCreator) creatorObj.unwrap();

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
    public void testFixTable() throws IOException {

        String js = "ci.fixTable([\"col1\", \"col2\"], [[\"row1\", \"row2\"],[\"row11\", \"row22\"]])";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        StringArrayFixTableCreator creator = (StringArrayFixTableCreator) creatorObj.unwrap();

        Assert.assertArrayEquals(creator.getColumnNames(), new String[]{"col1", "col2"});
        Assert.assertArrayEquals(creator.getData(), new String[][]{new String[]{"row1", "row2"}, new String[]{"row11", "row22"}});

    }

    @Test (expected = JavaScriptException.class)
    public void testFixTableNoRows() throws IOException {

        String js = "ci.fixTable([\"col1\", \"col2\"])";

        TestConfigurationParser parser = new TestConfigurationParser();

        parser.evaluateTestConfig(new StringReader(js), "string");
    }

    @Test
    public void testFixTableFromTSV() throws Exception {

        String js = "ci.fixTableFromTsv(ci.fixFile(\"A\\tB\\n1\\t2\\n11\\t22\"))";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        FileFixTableCreator creator = (FileFixTableCreator) creatorObj.unwrap();
        TestRun testRun = mock(TestRun.class);

        FixTable t = creator.create(testRun);
        FixTable.FixRow r1 = t.getRows().get(0);
        FixTable.FixRow r2 = t.getRows().get(1);
        Assert.assertEquals("1", r1.getCells().get("A"));
        Assert.assertEquals("2", r1.getCells().get("B"));
        Assert.assertEquals("11", r2.getCells().get("A"));
        Assert.assertEquals("22", r2.getCells().get("B"));
    }

    @Test(expected = JavaScriptException.class)
    public void testFixTableFromTSVError() throws IOException {
        String js = "ci.fixTableFromTsv()";
        TestConfigurationParser parser = new TestConfigurationParser();
        parser.evaluateTestConfig(new StringReader(js), "string");
    }
    
    @Test
    public void testFixFileFromHDFS() throws IOException {

        String js = "ci.fixFileFromHdfs(\"/foo\")";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        OutputFixDirFromHdfsCreator creator = (OutputFixDirFromHdfsCreator) creatorObj.unwrap();

        Assert.assertEquals(creator.getPath(), new Path("/foo")); // OutputFixDirFromHdfsCreator strips initial /
    }

    @Test(expected = JavaScriptException.class)
    public void testFixFileFromHDFSError() throws IOException {
        String js = "ci.fixFileFromHdfs()";
        TestConfigurationParser parser = new TestConfigurationParser();
        parser.evaluateTestConfig(new StringReader(js), "string");
    }
        
    @Test(expected = JavaScriptException.class)
    public void testFixTableFromResourceError() throws IOException {

        String js = "ci.fixTableFromResource()";

        TestConfigurationParser parser = new TestConfigurationParser();

        parser.evaluateTestConfig(new StringReader(js), "string");
    }


    @Test
    public void testHiveInputOnlyDbName() throws Exception {

        String js = "ci.hiveInput(\"dbname\")";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        HiveTableDeployer hiveTableDeployer = (HiveTableDeployer) creatorObj.unwrap();

        Assert.assertEquals(hiveTableDeployer.getDatabaseName(), new DatabaseName("dbname"));
        Assert.assertNull(hiveTableDeployer.getTableName());
        Assert.assertNull(hiveTableDeployer.getTableCreationScriptFile());
        Assert.assertNull(hiveTableDeployer.getDataFileCreator());
    }

    @Test
    public void testHiveInput() throws Exception {

        String tableCreationScript = "table creation script";
        String js = "ci.hiveInput(\"dbname\", \"tablename\", ci.fixFile(\"" + tableCreationScript + "\"))";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        HiveTableDeployer hiveTableDeployer = (HiveTableDeployer) creatorObj.unwrap();

        FixFile tableCreationFile = hiveTableDeployer.getTableCreationScriptFile().create(null);

        Assert.assertEquals(hiveTableDeployer.getDatabaseName(), new DatabaseName("dbname"));
        Assert.assertEquals(hiveTableDeployer.getTableName(), "tablename");
        Assert.assertEquals(IOUtils.toString(tableCreationFile.getContent()), tableCreationScript);
        Assert.assertNull(hiveTableDeployer.getDataFileCreator());
    }

    @Test
    public void testHiveInputWithData() throws Exception {

        String tableCreationScript = "table creation script";
        String js =
                "var table = ci.fixTable([\"col1\", \"col2\"], [[\"row1\", \"row2\"],[\"row11\", \"row22\"]]);" +
                "ci.hiveInput(\"dbname\", \"tablename\", ci.fixFile(\"" + tableCreationScript + "\"), table)";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        HiveTableDeployer hiveTableDeployer = (HiveTableDeployer) creatorObj.unwrap();

        Assert.assertEquals(hiveTableDeployer.getDatabaseName(), new DatabaseName("dbname"));
        Assert.assertEquals(hiveTableDeployer.getTableName(), "tablename");

        FixFile tableCreationFile = hiveTableDeployer.getTableCreationScriptFile().create(null);
        Assert.assertEquals(IOUtils.toString(tableCreationFile.getContent()), tableCreationScript);
        Assert.assertEquals(hiveTableDeployer.getDataFileCreator().getClass(), StringArrayFixTableCreator.class);
    }


    @Test(expected = JavaScriptException.class)
    public void testHiveInputFails() throws IOException {

        String js = "ci.hiveInput(\"tablename\", [[\"1\",\"2\",\"3\"],[\"11\",\"22\",\"33\"]])";

        TestConfigurationParser parser = new TestConfigurationParser();
        parser.evaluateTestConfig(new StringReader(js), "string");
    }

    @Test
    public void testHiveInputNoData() throws IOException {

        String js = "ci.hiveInput(\"dbname\", \"tablename\", ci.fixFile(\"create table blah\"))";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        HiveTableDeployer hiveTableDeployer = (HiveTableDeployer) creatorObj.unwrap();

        Assert.assertEquals(hiveTableDeployer.getDatabaseName(), new DatabaseName("dbname"));
        Assert.assertEquals(hiveTableDeployer.getTableName(), "tablename");

    }
    @Test
    public void testHiveTable() throws IOException {
        String js = "ci.hiveTable(\"dbname\", \"tablename\")";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        OutputFixTableFromHiveCreator creator = (OutputFixTableFromHiveCreator) creatorObj.unwrap();
        TestRun testRun = mock(TestRun.class);
        doReturn(UUID.nameUUIDFromBytes("fake".getBytes())).when(testRun).getTestUUID();
        Assert.assertEquals("Hive table celosci_dbname_144c9def_ac04_369c_bbfa_d8efaa8ea194.tablename", creator.getDescription(testRun));
    }

    @Test (expected = JavaScriptException.class)
    public void testHiveTableNoTable() throws IOException {
        String js = "ci.hiveTable(\"dbname\")";

        TestConfigurationParser parser = new TestConfigurationParser();
        parser.evaluateTestConfig(new StringReader(js), "string");
    }

    @Test (expected = JavaScriptException.class)
    public void testHiveTableNoDb() throws IOException {
        String js = "ci.hiveTable()";

        TestConfigurationParser parser = new TestConfigurationParser();
        parser.evaluateTestConfig(new StringReader(js), "string");
    }

    @Test
    public void testTableToJson() throws IOException {
        String js = "ci.tableToJson(ci.hiveTable(\"dbname\", \"tablename\"))";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        ConversionCreator creator = (ConversionCreator) creatorObj.unwrap();

        Assert.assertEquals(FixTableToJsonFileConverter.class, creator.getFixObjectConverter().getClass());

    }

    @Test (expected = JavaScriptException.class)
    public void testTableToJsonNoCreator() throws IOException {
        String js = "ci.tableToJson()";

        TestConfigurationParser parser = new TestConfigurationParser();
        parser.evaluateTestConfig(new StringReader(js), "string");
    }
    
    @Test
    public void testTableToTSV() throws IOException {
        String js = "ci.tableToTSV(ci.hiveTable(\"dbname\", \"tablename\"))";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        ConversionCreator creator = (ConversionCreator) creatorObj.unwrap();

        Assert.assertEquals(FixTableToTSVFileConverter.class, creator.getFixObjectConverter().getClass());

    }

    @Test (expected = JavaScriptException.class)
    public void testTableToTSVNoCreator() throws IOException {
        String js = "ci.tableToTSV()";

        TestConfigurationParser parser = new TestConfigurationParser();
        parser.evaluateTestConfig(new StringReader(js), "string");
    }


    @Test
    public void testExpandJson() throws IOException {
        String js = "ci.expandJson(ci.tableToJson(ci.hiveTable(\"dbname\", \"tablename\")), [\"field1\", \"field2\"])";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        ConversionCreator creator = (ConversionCreator) creatorObj.unwrap();
        Assert.assertEquals(JsonExpandConverter.class, creator.getFixObjectConverter().getClass());
    }


    @Test
    public void testExpandJsonNoFields() throws IOException {
        String js = "ci.expandJson(ci.tableToJson(ci.hiveTable(\"dbname\", \"tablename\")))";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        ConversionCreator creator = (ConversionCreator) creatorObj.unwrap();
        Assert.assertEquals(JsonExpandConverter.class, creator.getFixObjectConverter().getClass());
    }

    @Test (expected = JavaScriptException.class)
    public void testExpandJsonNoParams() throws IOException {
        String js = "ci.expandJson()";

        TestConfigurationParser parser = new TestConfigurationParser();
        parser.evaluateTestConfig(new StringReader(js), "string");
    }

    @Test
    public void testFixTableComparerNotOrdered() throws IOException {
        String js =
                "var table1 = ci.fixTable([\"col1\", \"col2\"], [[\"row1\", \"row2\"],[\"row11\", \"row22\"]]);" +
                        "var table2 = ci.fixTable([\"col1\", \"col2\"], [[\"row1\", \"row2\"],[\"row11\", \"row22\"]]);" +
                        "ci.fixTableCompare(table1, table2);";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        FixTableComparer comparer = (FixTableComparer) creatorObj.unwrap();
        Assert.assertEquals(false, comparer.isColumnNamesOrdered());
        Assert.assertEquals(false, comparer.isRespectRowOrder());
    }

    @Test
    public void testFixTableComparerOrdered() throws IOException {
        String js =
                "var table1 = ci.fixTable([\"col1\", \"col2\"], [[\"row1\", \"row2\"],[\"row11\", \"row22\"]]);" +
                        "var table2 = ci.fixTable([\"col1\", \"col2\"], [[\"row1\", \"row2\"],[\"row11\", \"row22\"]]);" +
                        "ci.fixTableCompare(table1, table2, true, true);";

        TestConfigurationParser parser = new TestConfigurationParser();

        NativeJavaObject creatorObj = (NativeJavaObject) parser.evaluateTestConfig(new StringReader(js), "string");
        FixTableComparer comparer = (FixTableComparer) creatorObj.unwrap();
        Assert.assertEquals(true, comparer.isColumnNamesOrdered());
        Assert.assertEquals(true, comparer.isRespectRowOrder());

    }

    @Test (expected = JavaScriptException.class)
    public void testFixTableComparerFails1() throws IOException {
        String js =
                "var table1 = ci.fixTable([\"col1\", \"col2\"], [[\"row1\", \"row2\"],[\"row11\", \"row22\"]]);" +
                "ci.fixTableCompare(table1);";

        TestConfigurationParser parser = new TestConfigurationParser();
        parser.evaluateTestConfig(new StringReader(js), "string");
    }

    @Test (expected = JavaScriptException.class)
    public void testFixTableComparerFails2() throws IOException {
        String js = "ci.fixTableCompare();";

        TestConfigurationParser parser = new TestConfigurationParser();
        parser.evaluateTestConfig(new StringReader(js), "string");
    }

}
