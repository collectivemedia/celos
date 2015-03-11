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

var ci = {};

ci.fixDirFromResource = function (resource) {
    if (!resource) {
        throw "Undefined resource";
    }
    return new FixDirFromResourceCreator(resource);
}

ci.fixFileFromResource = function (resource) {
    if (!resource) {
        throw "Undefined resource";
    }
    return new FixFileFromResourceCreator(resource);
}

ci.fixFile = function (content) {
    if (!content) {
        throw "Undefined content";
    }
    return new FixFileFromStringCreator(content);
}

ci.fixDir = function (content) {
    if (!content) {
        throw "Undefined content";
    }
    return new FixDirHierarchyCreator(content);
}

ci.hdfsInput = function (fixObject, whereToPlace) {
    if (!fixObject) {
        throw "Undefined input fixObject";
    }
    if (!whereToPlace) {
        throw "Undefined input data path";
    }
    return new HdfsInputDeployer(fixObject, whereToPlace);
}

ci.fixFileFromHdfs = function (path) {
    if (!path) {
        throw "Undefined path";
    }
    return new OutputFixDirFromHdfsCreator(path);
}

ci.avroToJson = function (creatorOrPath) {
    if (!creatorOrPath) {
        throw "Undefined expected creatorOrPath";
    }
    if (typeof creatorOrPath == 'string') {
        creatorOrPath = new OutputFixDirFromHdfsCreator(creatorOrPath)
    }
    return new ConversionCreator(creatorOrPath, new FixDirRecursiveConverter(new AvroToJsonConverter()));
}

ci.jsonToAvro = function (dirCreator, schemaFileCreator) {
    if (!dirCreator) {
        throw "Undefined expected dirCreator";
    }
    if (!schemaFileCreator) {
        throw "Undefined expected schemaFileCreator";
    }
    return new ConversionCreator(dirCreator, new FixDirRecursiveConverter(new JsonToAvroConverter(schemaFileCreator)));
}


ci.jsonCompare = function (expectedCreator, actualCreator, ignorePathsRaw) {
    if (!expectedCreator) {
        throw "Undefined expectedCreator";
    }
    if (!actualCreator) {
        throw "Undefined actualCreator";
    }
    var ignorePaths = new HashSet();

    if (ignorePathsRaw) {
        for (var i=0; i < ignorePathsRaw.length; i++) {
            ignorePaths.add(ignorePathsRaw[i]);
        }
    }
    return new JsonContentsComparer(ignorePaths, expectedCreator, actualCreator);
}

ci.plainCompare = function (fixObjectCreator, path) {
    if (!fixObjectCreator) {
        throw "Undefined expected fixObject";
    }
    if (!path) {
        throw "Undefined path";
    }
    return new RecursiveFsObjectComparer(fixObjectCreator, new OutputFixDirFromHdfsCreator(path));
}

ci.fixTableCompare = function (expectedCreator, actualCreator, columnNamesOrdered, rowsOrdered) {
    if (!expectedCreator) {
        throw "Undefined expectedCreator";
    }
    if (!actualCreator) {
        throw "Undefined actualCreator";
    }
    if (!columnNamesOrdered) {
        columnNamesOrdered = false;
    }
    if (!rowsOrdered) {
        rowsOrdered = false;
    }
    return new FixTableComparer(expectedCreator, actualCreator, columnNamesOrdered, rowsOrdered);
}

ci.addTestCase = function (testCase) {
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

ci.fixTableFromResource = function (path) {
    if (!path) {
        throw "path undefined";
    }
    return ci.fixTableFromTsv(ci.fixFileFromResoure(path));
}

ci.fixTableFromTsv = function (fileCreator) {
    if (!fileCreator) {
        throw "fileCreator undefined";
    }
    return new FileFixTableCreator(fileCreator);
}

ci.fixTable = function (columnNames, rowData) {
    if (!columnNames) {
        throw "columnNames undefined";
    }
    if (!rowData) {
        throw "rowData undefined";
    }
    return new StringArrayFixTableCreator(columnNames, rowData);
}

ci.hiveInput = function (dbName, tableName, createScriptFile, fixTableCreator) {
    if (!dbName || typeof dbName != "string") {
        throw "dbName should be valid string";
    }
    if (!tableName) {
        tableName = null;
        createScriptFile = null;
        fixTableCreator = null;
    } else {
        if (!createScriptFile) {
            throw "createScriptFile should be valid string";
        }
        if (!fixTableCreator) {
            fixTableCreator = null;
        }
    }

    return new HiveTableDeployer(new DatabaseName(dbName), tableName, createScriptFile, fixTableCreator);
}

ci.hiveTable = function (databaseName, tableName) {
    if (!databaseName) {
        throw "databaseName should be valid string";
    }
    if (!tableName) {
        throw "tableName should be valid string";
    }
    return new OutputFixTableFromHiveCreator(new DatabaseName(databaseName), tableName);
}

ci.tableToJson = function (fixTableCreator) {
    if (!fixTableCreator) {
        throw "Undefined fixTableCreator";
    }
    return new ConversionCreator(fixTableCreator, new FixTableToJsonFileConverter());
}

ci.tableToTSV = function (fixTableCreator) {
    if (!fixTableCreator) {
        throw "Undefined fixTableCreator";
    }
    return new ConversionCreator(fixTableCreator, new FixTableToTSVFileConverter());
}

ci.expandJson = function (jsonCreator, fieldsRaw) {
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