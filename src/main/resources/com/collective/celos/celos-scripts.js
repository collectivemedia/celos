importPackage(Packages.com.collective.celos);
importPackage(Packages.com.collective.celos.trigger);
importPackage(Packages.com.collective.celos.ci.testing);

// FIXME: temporary solution: until all utility functions return real Java objects,
// allow JSON also and create instances from it using the JSONInstanceCreator.
function addWorkflow(json) {
    if (typeof(json.id) !== "string") {
        throw "Workflow ID must be a string: " + json.id;
    }
    celosWorkflowConfigurationParser.addWorkflow(
        new Workflow(
            new WorkflowID(json.id),
            json.schedule,
            json.schedulingStrategy,
            json.trigger,
            json.externalService,
            json.maxRetryCount ? json.maxRetryCount : 0,
            new ScheduledTime(json.startTime ? json.startTime : "1970-01-01T00:00:00.000Z")
        ),
        celosWorkflowConfigFilePath
    );
}

function importDefaults(label) {
    celosWorkflowConfigurationParser.importDefaultsIntoScope(label, celosScope);
}

function hourlySchedule() {
    return new HourlySchedule();
}

function minutelySchedule() {
    return new MinutelySchedule();
}

function cronSchedule(cronExpression) {
    if (!cronExpression) {
        throw "Undefined cron expression";
    }
    return new CronSchedule(cronExpression);
}

function serialSchedulingStrategy(concurrency) {
    return new SerialSchedulingStrategy(concurrency === undefined ? 1 : concurrency);
}

function alwaysTrigger() {
    return new AlwaysTrigger();
}

function hdfsCheck(path, slotID, fs) {
    var trigger = hdfsCheckTrigger(path, fs);
    var scheduledTime;

    if (!slotID) {
        scheduledTime = ScheduledTime.now();
    } else {
        if (typeof slotID != "object" || slotID.getClass().getName() !== "com.collective.celos.SlotID") {
            throw "slotID should be instance of com.collective.celos.SlotID";
        }
        scheduledTime = slotID.getScheduledTime();
    }
    return trigger.isDataAvailable(null, ScheduledTime.now(), scheduledTime);
}

// Pass fs as last parameter so we can later use a default if parameter not supplied
function hdfsCheckTrigger(path, fs) {
    if (!path) {
        throw "Undefined path in hdfsCheckTrigger";
    }
    if (!fs) {
        if (typeof CELOS_DEFAULT_HDFS !== "undefined") {
            fs = CELOS_DEFAULT_HDFS;
        } else {
            throw "Undefined fs in hdfsCheckTrigger and CELOS_DEFAULT_HDFS not set";
        }
    }
    return new HDFSCheckTrigger(path, fs);
}

function andTrigger() {
    var list = new Packages.java.util.LinkedList();
    for (var i = 0; i < arguments.length; i++) {
        list.add(arguments[i]);
    }
    return new AndTrigger(list);
}

function orTrigger() {
    var list = new Packages.java.util.LinkedList();
    for (var i = 0; i < arguments.length; i++) {
        list.add(arguments[i]);
    }
    return new OrTrigger(list);
}

function notTrigger(subTrigger) {
    if (!subTrigger) {
        throw "Undefined sub trigger";
    }
    return new NotTrigger(subTrigger);
}

function delayTrigger(seconds) {
    if (!seconds) {
        throw "Undefined seconds";
    }
    return new DelayTrigger(seconds);
}

function offsetTrigger(seconds, trigger) {
    if (!seconds) {
        throw "Undefined seconds";
    }
    if (!trigger) {
        throw "Undefined trigger";
    }
    return new OffsetTrigger(seconds, trigger);
}

function commandTrigger() {
    var list = new Packages.java.util.LinkedList();
    for (var i = 0; i < arguments.length; i++) {
        list.add(arguments[i]);
    }
    return new CommandTrigger(list);
}

function successTrigger(workflowName) {
    if (!workflowName) {
        throw "Undefined workflow name in success trigger";
    }
    return new SuccessTrigger(workflowName);
}

function mergeProperties(source, target) {
    for (var name in source) {
        target[name] = source[name];
    }
}

function hdfsPath(path) {
    if (!path) {
        throw "Undefined path in hdfsPath";
    }
    if (typeof HDFS_PREFIX_JS_VAR !== "undefined") {
        return Packages.com.collective.celos.Util.augmentHdfsPath(HDFS_PREFIX_JS_VAR, path);
    } else {
        return path;
    }
}

function oozieExternalService(userPropertiesOrFun, oozieURL) {
    if (oozieURL === undefined) {
        if (typeof CELOS_DEFAULT_OOZIE !== "undefined") {
            oozieURL = CELOS_DEFAULT_OOZIE;
        } else {
            throw "Undefined Oozie URL";
        }
    }
    var propertiesGen = makePropertiesGen(userPropertiesOrFun);
    return new OozieExternalService(oozieURL, propertiesGen);
}

function makePropertiesGen(userPropertiesOrFun) {
    // If user passes in a function, use it as the properties
    // generator.  Otherwise create a a function that always
    // returns the passed in object.
    var userFun = (typeof userPropertiesOrFun === "function")
        ? userPropertiesOrFun 
        : function(ignoredSlotID) { return userPropertiesOrFun; };
    function getPropertiesFun(slotID) {
        var userProperties = userFun(slotID);
        var theProperties = {};
        if (typeof CELOS_DEFAULT_OOZIE_PROPERTIES !== "undefined") {
            mergeProperties(CELOS_DEFAULT_OOZIE_PROPERTIES, theProperties);
        }
        mergeProperties(userProperties, theProperties);
        if (typeof USERNAME !== "undefined") {
            theProperties["user.name"] = USERNAME;
        }

        return celosMapper.readTree(JSON.stringify(theProperties));
    }
    return new PropertiesGenerator({ getProperties: getPropertiesFun });
}

function commandExternalService(command) {
    if (!command) {
        throw "Undefined command";
    }
    // FIXME: the wrapper and /var/lib path should probably be specified elsewhere
    return new CommandExternalService(command, "celos-wrapper", "/var/lib/celos/jobs");
}

function replaceTimeVariables(string, t) {
    string = string.replace(/\${year}/g, t.year());
    string = string.replace(/\${month}/g, t.month());
    string = string.replace(/\${day}/g, t.day());
    string = string.replace(/\${hour}/g, t.hour());
    string = string.replace(/\${minute}/g, t.minute());
    string = string.replace(/\${second}/g, t.second());
    return string;                            
}

function databaseName(database) {
    if (typeof HDFS_PREFIX_JS_VAR !== "undefined") {
        return new DatabaseName(database).getMockedName(TEST_UUID_JS_VAR);
    } else {
        return database;
    }
}
