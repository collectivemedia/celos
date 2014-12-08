importPackage(Packages.com.collective.celos.ci.testing.fixtures.create);
importPackage(Packages.com.collective.celos.ci.testing.fixtures.deploy);
importPackage(Packages.com.collective.celos.ci.testing.fixtures.compare);
importPackage(Packages.com.collective.celos.ci.mode.test);
importPackage(Packages.com.collective.celos.ci.testing.fixtures.convert.avro);
importPackage(Packages.com.collective.celos.ci.testing.structure.fixobject);
importPackage(Packages.java.util);
importPackage(Packages.com.collective.celos.ci.testing.fixtures.compare.json);

function fixDirFromResource(resource) {
    if (!resource) {
        throw "Undefined resource";
    }
    return new FixDirFromResourceCreator(commandLine.getTestCasesDir(), resource);
}

function fixFileFromResource(resource) {
    if (!resource) {
        throw "Undefined resource";
    }
    return new FixFileFromResourceCreator(commandLine.getTestCasesDir(), resource);
}

function fixFile(content) {
    if (!content) {
        throw "Undefined content";
    }
    return new FixFileFromStringCreator(content);
}

function fixDir(hier) {
    if (!content) {
        throw "Undefined content";
    }
    return new FixDirHierarchyCreator(hier);
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

function avroToJson(avroOrPath) {
    if (!avroOrPath) {
        throw "Undefined expected avroOrPath";
    }
    if (typeof avroOrPath == 'string') {
        avroOrPath = new OutputFixDirFromHdfsCreator(avroOrPath)
    }
    return new FixDirTreeConverter(avroOrPath, new AvroToJsonConverter());
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
    return new JsonContentsDirComparer(ignorePaths, expectedCreator, actualCreator);
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
    testConfigurationParser.addTestCase(result);
}