if (typeof FOO !== "undefined") throw "FOO already set";
if (typeof BAR !== "undefined") throw "BAR already set";
if (typeof myFunction !== "undefined") throw "myFunction already set";

importDefaults("test");

if (FOO !== 1) throw "FOO was not imported";
if (BAR !== 2) throw "BAR was not imported";
if (myFunction() !== 2) throw "myFunction was not imported";
