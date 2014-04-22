importPackage(Packages.com.collective.celos);

// FIXME: temporary solution: until all utility functions return real Java objects,
// allow JSON also and create instances from it using the JSONInstanceCreator.
function addWorkflow(json) {
    if (typeof(json.id) !== "string") {
        throw "Workflow ID must be a string: " + json.id;
    }
    celosWorkflowConfigurationParser.addWorkflow(
        new Workflow(
            new WorkflowID(json.id),
            json.schedule instanceof Schedule ? json.schedule
                : celosCreator.createInstance(JSON.stringify(json.schedule)),
            json.schedulingStrategy instanceof SchedulingStrategy ? json.schedulingStrategy
                : celosCreator.createInstance(JSON.stringify(json.schedulingStrategy)),
            json.trigger instanceof Trigger ? json.trigger
                : celosCreator.createInstance(JSON.stringify(json.trigger)),
            json.externalService instanceof ExternalService ? json.externalService
                : celosCreator.createInstance(JSON.stringify(json.externalService)),
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
    return new CronSchedule(cronExpression);
}

function serialSchedulingStrategy(concurrency) {
    return new SerialSchedulingStrategy(concurrency === undefined ? 1 : concurrency);
}

function alwaysTrigger() {
    return new AlwaysTrigger();
}

// Pass fs as final parameter so we can later use a default if parameter not supplied
function hdfsCheckTrigger(path, fs) {
    if (fs === undefined) {
        if (typeof CELOS_DEFAULT_HDFS !== "undefined") {
            fs = CELOS_DEFAULT_HDFS;
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

function notTrigger(subTrigger) {
    return new NotTrigger(subTrigger);
}

function delayTrigger(seconds) {
    return new DelayTrigger(seconds);
}

function commandTrigger() {
    var list = new Packages.java.util.LinkedList();
    for (var i = 0; i < arguments.length; i++) {
        list.add(arguments[i]);
    }
    return new CommandTrigger(list);
}

function successTrigger(workflowName) {
    return {
        "type": "com.collective.celos.SuccessTrigger",
        "properties": {
            "celos.successTrigger.workflow": workflowName
        }
    };
}

function mergeProperties(source, target) {
    for (var name in source) {
        target[name] = source[name];
    }
}

function oozieExternalService(userPropertiesOrFun, oozieURL) {
    if (oozieURL === undefined) {
        if (typeof CELOS_DEFAULT_OOZIE !== "undefined") {
            oozieURL = CELOS_DEFAULT_OOZIE;
        } else {
            throw "oozieURL is undefined";
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
        return celosMapper.readTree(JSON.stringify(theProperties));
    }
    return new PropertiesGenerator({ getProperties: getPropertiesFun });
}

function commandExternalService(command) {
    return {
        "type": "com.collective.celos.CommandExternalService",
        "properties": {
            "celos.commandExternalService.command": command,
            "celos.commandExternalService.wrapperCommand": "celos-wrapper",
            "celos.commandExternalService.databaseDir": "/var/lib/celos/jobs"
        }
    };
}
