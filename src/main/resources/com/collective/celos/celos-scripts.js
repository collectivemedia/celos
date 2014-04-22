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
    return {
        "type": "com.collective.celos.MinutelySchedule"
    };
}

function cronSchedule(cronExpression) {
    return {
        "type": "com.collective.celos.CronSchedule",
        "properties": {
            "celos.cron.config": cronExpression
        }
    };
}

function serialSchedulingStrategy(concurrency) {
    if(concurrency === undefined){
        concurrency = 1;
    }
    return {
        "type": "com.collective.celos.SerialSchedulingStrategy",
        "properties": {
            "celos.serial.concurrency": concurrency
        }

    };
}

function alwaysTrigger() {
    return {
        "type": "com.collective.celos.AlwaysTrigger"
    };
}

// Pass fs as final parameter so we can later use a default if parameter not supplied
function hdfsCheckTrigger(path, fs) {
    if (fs === undefined) {
        if (typeof CELOS_DEFAULT_HDFS !== "undefined") {
            fs = CELOS_DEFAULT_HDFS;
        }
    }
    return {
        "type": "com.collective.celos.HDFSCheckTrigger",
        "properties": {
            "celos.hdfs.fs": fs,
            "celos.hdfs.path": path
        }
    };
}

function andTrigger() {
    return {
        "type": "com.collective.celos.AndTrigger",
        "properties": {
            "celos.andTrigger.triggers": Array.prototype.slice.call(arguments)
        }
    };
}

function notTrigger(subTrigger) {
    return {
        "type": "com.collective.celos.NotTrigger",
        "properties": {
            "celos.notTrigger.trigger": subTrigger
        }
    };
}

function delayTrigger(seconds) {
    return {
        "type": "com.collective.celos.DelayTrigger",
        "properties": {
            "celos.delayTrigger.seconds": seconds
        }
    };
}

function commandTrigger() {
    return {
        "type": "com.collective.celos.CommandTrigger",
        "properties": {
            "celos.commandTrigger.command": Array.prototype.slice.call(arguments)
        }
    };
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
