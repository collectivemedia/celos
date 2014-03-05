addWorkflow({
    "id": "workflow-1",
    "schedule": {
        "type": "com.collective.celos.CronSchedule",
        "properties": {
            "celos.cron.config": "0 12 * * * ?"
        }
    },
    "schedulingStrategy": {
        "type": "com.collective.celos.SerialSchedulingStrategy"
    },
    "trigger": {
        "type": "com.collective.celos.HDFSCheckTrigger",
        "properties": {
            "celos.hdfs.path": "foo",
            "celos.hdfs.fs": "file:///"
        }
    },
    "externalService": {
        "type": "com.collective.celos.OozieExternalService",
        "properties": {
            "celos.oozie.url": "oj01/oozie"
        }
    },
    "maxRetryCount": 0
});
