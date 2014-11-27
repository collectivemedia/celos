importPackage(Packages.com.collective.celos.ci.testing.fixtures.create);
importPackage(Packages.com.collective.celos.ci.testing.fixtures.deploy);
importPackage(Packages.com.collective.celos.ci.testing.fixtures.compare);
importPackage(Packages.com.collective.celos.ci.mode.test);

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

function hdfsOutput(fixObject, result) {
    if (!fixObject) {
        throw "Undefined expected fixObject";
    }
    if (!result) {
        throw "Undefined actual data path";
    }
    var actualCreator = new FixDirFromHdfsCreator(result);
    return new RecursiveDirComparer(fixObject, actualCreator);
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