importPackage(Packages.com.collective.celos.ci.testing.fixtures.create);
importPackage(Packages.com.collective.celos.ci.testing.fixtures.deploy);
importPackage(Packages.com.collective.celos.ci.testing.fixtures.compare);
importPackage(Packages.com.collective.celos.ci.testing.config);

function fixDirFromResource(resource) {
    return new FixDirFromResourceCreator(resource);
}

function fixFileFromResource(resource) {
    return new FixFileFromResourceCreator(resource);
}

function fixFile(content) {
    return new FixFileFromStringCreator(content);
}

function fixDir(hier) {
    return new FixDirHierarchyCreator(hier);
}

function hdfsInput(fixObject, whereToPlace) {
    return new HdfsInputDeployer(fixObject, whereToPlace, celosCiContext);
}

function hdfsOutput(fixObject, result) {
    var actualCreator = new FixDirFromHdfsCreator(celosCiContext, result);
    return new RecursiveDirComparer(fixObject, actualCreator);
}

function addTestCase(testCase) {
    var inputs = testCase["inputs"];
    var outputs = testCase["outputs"];
    var result = new TestCase(testCase["name"]);
    for (var i=0; i < inputs.length; i++) {
        result.addInput(inputs[i]);
    }
    for (var i=0; i < outputs.length; i++) {
        result.addOutput(outputs[i]);
    }
    testConfigurationParser.addTestCase(result);
}