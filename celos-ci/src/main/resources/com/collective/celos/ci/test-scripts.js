importPackage(Packages.com.collective.celos.ci.fixtures);
importPackage(Packages.com.collective.celos.ci);

// FIXME: temporary solution: until all utility functions return real Java objects,
// allow JSON also and create instances from it using the JSONInstanceCreator.
function runTestCase(json) {
    if (!json) {
        throw "undefined test definition json";
    }
    if (typeof json.input === "undefined") {
        throw "undefined test input";
    }


}

function