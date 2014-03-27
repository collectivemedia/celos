function addWorkflow(json) {
    celosWorkflowConfigurationParser.addWorkflowFromJSONString(JSON.stringify(json));
}

function importDefaults(label) {
    celosWorkflowConfigurationParser.importDefaultsIntoScope(label, celosScope);
}

function hourlySchedule() {
    return {
        "type": "com.collective.celos.HourlySchedule"
    };
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

function serialSchedulingStrategy() {
    return {
        "type": "com.collective.celos.SerialSchedulingStrategy"
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

function delayTrigger(seconds) {
    return {
        "type": "com.collective.celos.DelayTrigger",
        "properties": {
            "celos.delayTrigger.seconds": seconds
        }
    };
}

function shellCommandTrigger() {
    return {
        "type": "com.collective.celos.ShellCommandTrigger",
        "properties": {
            "celos.shellCommandTrigger.command": Array.prototype.slice.call(arguments)
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

function oozieExternalService(userProperties, oozieURL) {
    function mergeProperties(source, target) {
        for (var name in source) {
            target[name] = source[name];
        }
    }
    if (oozieURL === undefined) {
        if (typeof CELOS_DEFAULT_OOZIE !== "undefined") {
            oozieURL = CELOS_DEFAULT_OOZIE;
        }
    }
    var theProperties = {
        "celos.oozie.url": oozieURL
    };
    if (typeof CELOS_DEFAULT_OOZIE_PROPERTIES !== "undefined") {
        mergeProperties(CELOS_DEFAULT_OOZIE_PROPERTIES, theProperties);
    }
    mergeProperties(userProperties, theProperties)
    return {
        "type": "com.collective.celos.OozieExternalService",
        "properties": theProperties
    };
}

function commandExternalService(command) {
    return {
        "type": "com.collective.celos.CommandExternalService",
        "properties": {
            "celos.commandExternalService.command": command,
            "celos.commandExternalService.outerWrapperCommand": "celos-outer-wrapper",
            "celos.commandExternalService.innerWrapperCommand": "celos-inner-wrapper",
            "celos.commandExternalService.databaseDir": "/var/lib/celos/jobs"
        }
    };
}
