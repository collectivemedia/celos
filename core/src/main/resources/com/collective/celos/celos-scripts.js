function addWorkflow(json) {
    celosWorkflowConfigurationParser.addWorkflowFromJSONString(JSON.stringify(json));
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
        if (typeof CELOS_DEFAULT_HDFS !== undefined) {
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

// Pass oozieURL separately so we later use a default if parameter not supplied
function oozieExternalService(properties, oozieURL) {
    properties["celos.oozie.url"] = oozieURL;
    return {
        "type": "com.collective.celos.OozieExternalService",
        "properties": properties
    };
}
