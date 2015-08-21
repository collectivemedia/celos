// Make sure variable from workflow-1 isn't visible
if (typeof FOO !== "undefined") throw "FOO leaked from workflow-1";