addWorkflow({
    "id": "workflow-1",
    "schedule": {
        "type": "com.collective.celos.HourlySchedule",
        "properties": {
            "a": "1",
            "b": "2"
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
