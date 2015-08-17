importPackage(Packages.com.collective.celos);
importPackage(Packages.com.collective.celos.trigger);
importPackage(Packages.com.collective.celos.ci.testing);
var celos = {};

// FIXME: temporary solution: until all utility functions return real Java objects,
// allow JSON also and create instances from it using the JSONInstanceCreator.
celos.addWorkflow = function (json) {

    function createWorkflowInfo(json) {
        var contacts = new Packages.java.util.ArrayList();
        if (json.contacts) {
            for (var i = 0; i < json.contacts.length; i++) {
                var email = json.contacts[i].email;
                if (!email) {
                    email = null;
                }
                var name = json.contacts[i].name || null;
                contacts.add(new WorkflowInfo.ContactsInfo(name, email));
            }
        }
        var url = null;
        if (json.url) {
            url = new Packages.java.net.URL(json.url);
        }
        return new WorkflowInfo(url, contacts);
    }


    if (typeof(json.id) !== "string") {
        throw "Workflow ID must be a string: " + json.id;
    }

    var waitTimeoutSeconds;
    if (typeof(json.waitTimeoutSeconds) === "undefined") {
        waitTimeoutSeconds = Workflow.DEFAULT_WAIT_TIMEOUT_SECONDS;
    } else {
        if (typeof(json.waitTimeoutSeconds) === "number") {
            waitTimeoutSeconds = json.waitTimeoutSeconds;
        } else {
            throw "waitTimeoutSeconds must be a number: " + json.waitTimeoutSeconds;
        }
    }

    var workflowInfo = createWorkflowInfo(json);
    var workflow = new Workflow(
            new WorkflowID(json.id),
            json.schedule,
            json.schedulingStrategy,
            json.trigger,
            json.externalService,
            json.maxRetryCount ? json.maxRetryCount : 0,
            new ScheduledTime(json.startTime ? json.startTime : "1970-01-01T00:00:00.000Z"),
            waitTimeoutSeconds,
            workflowInfo
    );

    celosWorkflowConfigurationParser.addWorkflow(workflow);
}


celos.importDefaults = function (label) {
    celosWorkflowConfigurationParser.importDefaultsIntoScope(label, celosScope);
}

celos.hourlySchedule = function () {
    return new HourlySchedule();
}

celos.minutelySchedule = function () {
    return new MinutelySchedule();
}

celos.cronSchedule = function (cronExpression) {
    if (!cronExpression) {
        throw "Undefined cron expression";
    }
    return new CronSchedule(cronExpression);
}

celos.dependentSchedule = function (workflowID) {
    return new DependentSchedule(new WorkflowID(workflowID));
}

celos.serialSchedulingStrategy = function (concurrency) {
    return new SerialSchedulingStrategy(concurrency === undefined ? 1 : concurrency);
}

celos.alwaysTrigger = function () {
    return new AlwaysTrigger();
}

celos.hdfsCheck = function (path, slotID, fs) {
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
celos.hdfsCheckTrigger = function (path, fs) {
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

celos.andTrigger = function () {
    var list = new Packages.java.util.LinkedList();
    for (var i = 0; i < arguments.length; i++) {
        list.add(arguments[i]);
    }
    return new AndTrigger(list);
}

celos.orTrigger = function () {
    var list = new Packages.java.util.LinkedList();
    for (var i = 0; i < arguments.length; i++) {
        list.add(arguments[i]);
    }
    return new OrTrigger(list);
}

celos.notTrigger = function (subTrigger) {
    if (!subTrigger) {
        throw "Undefined sub trigger";
    }
    return new NotTrigger(subTrigger);
}

celos.delayTrigger = function (seconds) {
    if (!seconds) {
        throw "Undefined seconds";
    }
    return new DelayTrigger(seconds);
}

celos.offsetTrigger = function (seconds, trigger) {
    if (!seconds) {
        throw "Undefined seconds";
    }
    if (!trigger) {
        throw "Undefined trigger";
    }
    return new OffsetTrigger(seconds, trigger);
}

celos.successTrigger = function (workflowName) {
    if (!workflowName) {
        throw "Undefined workflow name in success trigger";
    }
    return new SuccessTrigger(workflowName);
}

celos.mergeProperties = function (source, target) {
    for (var name in source) {
        target[name] = source[name];
    }
}

celos.hdfsPath = function (path) {
    if (!path) {
        throw "Undefined path in hdfsPath";
    }
    if (typeof HDFS_PREFIX_JS_VAR !== "undefined") {
        return Packages.com.collective.celos.Util.augmentHdfsPath(HDFS_PREFIX_JS_VAR, path);
    } else {
        return path;
    }
}

celos.makePropertiesGen = function (userPropertiesOrFun) {
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
            celos.mergeProperties(CELOS_DEFAULT_OOZIE_PROPERTIES, theProperties);
        }
        celos.mergeProperties(userProperties, theProperties);
        if (typeof CELOS_USER_JS_VAR !== "undefined") {
            theProperties["user.name"] = CELOS_USER_JS_VAR;
        }

        return celosMapper.readTree(JSON.stringify(theProperties));
    }
    return new PropertiesGenerator({ getProperties: getPropertiesFun });
}


celos.oozieExternalService = function (userPropertiesOrFun, oozieURL) {

    if (oozieURL === undefined) {
        if (typeof CELOS_DEFAULT_OOZIE !== "undefined") {
            oozieURL = CELOS_DEFAULT_OOZIE;
        } else {
            throw "Undefined Oozie URL";
        }
    }
    var propertiesGen = celos.makePropertiesGen(userPropertiesOrFun);
    return new OozieExternalService(oozieURL, propertiesGen);
}

celos.replaceTimeVariables = function (string, t) {
    string = string.replace(/\${year}/g, t.year());
    string = string.replace(/\${month}/g, t.month());
    string = string.replace(/\${day}/g, t.day());
    string = string.replace(/\${hour}/g, t.hour());
    string = string.replace(/\${minute}/g, t.minute());
    string = string.replace(/\${second}/g, t.second());
    return string;                            
}

celos.databaseName = function (database) {
    if (typeof HDFS_PREFIX_JS_VAR !== "undefined") {
        return new DatabaseName(database).getMockedName(TEST_UUID_JS_VAR);
    } else {
        return database;
    }
}

celos.isRunningInTestMode = function() {
    return (typeof HDFS_PREFIX_JS_VAR !== "undefined");
}

var addWorkflow = celos.addWorkflow;
var importDefaults = celos.importDefaults;
var hourlySchedule = celos.hourlySchedule;
var minutelySchedule = celos.minutelySchedule;
var cronSchedule = celos.cronSchedule;
var dependentSchedule = celos.dependentSchedule;
var serialSchedulingStrategy = celos.serialSchedulingStrategy;
var alwaysTrigger = celos.alwaysTrigger;
var hdfsCheck = celos.hdfsCheck;
var hdfsCheckTrigger = celos.hdfsCheckTrigger;
var andTrigger = celos.andTrigger;
var orTrigger = celos.orTrigger;
var notTrigger = celos.notTrigger;
var delayTrigger = celos.delayTrigger;
var offsetTrigger = celos.offsetTrigger;
var successTrigger = celos.successTrigger;
var mergeProperties = celos.mergeProperties;
var hdfsPath = celos.hdfsPath;
var oozieExternalService = celos.oozieExternalService;
var replaceTimeVariables = celos.replaceTimeVariables;
var databaseName = celos.databaseName;