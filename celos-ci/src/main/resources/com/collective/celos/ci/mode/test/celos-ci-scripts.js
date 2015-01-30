importPackage(Packages.com.collective.celos.ci.testing.fixtures.convert);
importPackage(Packages.com.collective.celos.ci.testing.fixtures.create);
importPackage(Packages.com.collective.celos.ci.testing.fixtures.deploy);
importPackage(Packages.com.collective.celos.ci.testing.fixtures.compare);
importPackage(Packages.com.collective.celos.ci.testing.structure.fixobject);
importPackage(Packages.com.collective.celos.ci.testing.fixtures.compare.json);
importPackage(Packages.com.collective.celos.ci.testing.fixtures.deploy.hive);
importPackage(Packages.com.collective.celos.ci.mode.test);
importPackage(Packages.com.collective.celos);
importPackage(Packages.java.util);

function fixDirFromResource(resource) {
    if (!resource) {
        throw "Undefined resource";
    }
    return new FixDirFromResourceCreator(resource);
}

function fixFileFromResource(resource) {
    if (!resource) {
        throw "Undefined resource";
    }
    return new FixFileFromResourceCreator(resource);
}

function fixFile(content) {
    if (!content) {
        throw "Undefined content";
    }
    return new FixFileFromStringCreator(content);
}

function fixDir(content) {
    if (!content) {
        throw "Undefined content";
    }
    return new FixDirHierarchyCreator(content);
}

function hdfsInput(fixObject, whereToPlace) {
    if (!fixObject) {
        throw "Undefined input fixObject";
    }
    if (!whereToPlace) {
        throw "Undefined input data path";
    }
    return new HdfsInputDeployer(fixObject, whereToPlace);
}

function avroToJson(creatorOrPath) {
    if (!creatorOrPath) {
        throw "Undefined expected creatorOrPath";
    }
    if (typeof creatorOrPath == 'string') {
        creatorOrPath = new OutputFixDirFromHdfsCreator(creatorOrPath)
    }
    return new ConversionCreator(creatorOrPath, new FixDirRecursiveConverter(new AvroToJsonConverter()));
}

function jsonToAvro(dirCreator, schemaFileCreator) {
    if (!dirCreator) {
        throw "Undefined expected dirCreator";
    }
    if (!schemaFileCreator) {
        throw "Undefined expected schemaFileCreator";
    }
    return new ConversionCreator(dirCreator, new FixDirRecursiveConverter(new JsonToAvroConverter(schemaFileCreator)));
}


function jsonCompare(expectedCreator, actualCreator, ignorePathsRaw) {
    if (!expectedCreator) {
        throw "Undefined expectedCreator";
    }
    if (!actualCreator) {
        throw "Undefined actualCreator";
    }
    ignorePaths = new HashSet();

    if (ignorePathsRaw) {
        for (var i=0; i < ignorePathsRaw.length; i++) {
            ignorePaths.add(ignorePathsRaw[i]);
        }
    }
    return new JsonContentsComparer(ignorePaths, expectedCreator, actualCreator);
}

function plainCompare(fixObjectCreator, path) {
    if (!fixObjectCreator) {
        throw "Undefined expected fixObject";
    }
    if (!path) {
        throw "Undefined path";
    }
    return new RecursiveDirComparer(fixObjectCreator, new OutputFixDirFromHdfsCreator(path));
}

function addTestCase(testCase) {
    var name = testCase["name"];

    var inputs = testCase["inputs"];
    var outputs = testCase["outputs"];
    var sampleTimeStart = testCase["sampleTimeStart"];
    var sampleTimeEnd = testCase["sampleTimeEnd"];
    var targetWorkflows = testCase["workflows"];

    if (!inputs) {
        throw "Undefined inputs";
    }
    if (!outputs) {
        throw "Undefined outputs";
    }
    if (!sampleTimeStart) {
        throw "Undefined sampleTimeStart";
    }
    if (!sampleTimeEnd) {
        throw "Undefined sampleTimeEnd";
    }

    var result = new TestCase(name, sampleTimeStart, sampleTimeEnd);

    for (var i=0; i < inputs.length; i++) {
        result.addInput(inputs[i]);
    }
    for (var i=0; i < outputs.length; i++) {
        result.addOutput(outputs[i]);
    }
    if (targetWorkflows) {
        for (var i=0; i < targetWorkflows.length; i++) {
            result.addTargetWorkflow(new WorkflowID(targetWorkflows[i]));
        }
    }
    testConfigurationParser.addTestCase(result);
}

function fixTableFromResource(fixTableFile) {
    if (!fixTableFile) {
        throw "fixTableFile undefined";
    }
    return new FileFixTableCreator(fixTableFile);
}

function fixTable(columnNames, rowData) {
    if (!columnNames) {
        throw "columnNames undefined";
    }
    if (!rowData) {
        throw "rowData undefined";
    }
    return new StringArrayFixTableCreator(columnNames, rowData);
}

function hiveInput(dbName, tableName, createScriptFile, fixTableCreator) {
    if (!dbName || typeof dbName != "string") {
        throw "dbName should be valid string";
    }
    if (!tableName || typeof tableName != "string") {
        throw "tableName should be valid string";
    }
    if (!createScriptFile) {
        throw "createScriptFile should be valid string";
    }
    if (!fixTableCreator) {
        fixTableCreator = null;
    }
    return new HiveTableDeployer(dbName, tableName, createScriptFile, fixTableCreator);
}

function hiveTable(databaseName, tableName) {
    if (!databaseName) {
        throw "databaseName should be valid string";
    }
    if (!tableName) {
        throw "tableName should be valid string";
    }
    return new OutputFixTableFromHiveCreator(databaseName, tableName);
}

function tableToJson(fixTableCreator) {
    if (!fixTableCreator) {
        throw "Undefined fixTableCreator";
    }
    return new ConversionCreator(fixTableCreator, new FixTableToJsonFileConverter());
}

function expandJson(jsonCreator, fieldsRaw) {
    if (!jsonCreator) {
        throw "Undefined jsonCreator";
    }
    fields = new HashSet();

    if (fieldsRaw) {
        for (var i=0; i < fieldsRaw.length; i++) {
            fields.add(fieldsRaw[i]);
        }
    }

    return new ConversionCreator(jsonCreator, new JsonExpandConverter(fields));
}