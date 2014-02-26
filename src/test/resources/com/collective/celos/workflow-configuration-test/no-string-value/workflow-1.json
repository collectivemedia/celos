{
    "id": "workflow",
    "schedule": {
        "type": "com.collective.celos.HourlySchedule",
        "properties": {
            "foo": [ "this is not a", "string" ]
        }
    },
    "schedulingStrategy": {
        "type": "com.collective.celos.SerialSchedulingStrategy" 
    },
    "trigger": {
        "type": "com.collective.celos.HDFSCheckTrigger",
        "properties": {
            "celos.hdfs.fs": "file:///",
            "celos.hdfs.path": "/logs/nym1/dorado/${year}-${month}-${day}/${hour}00/_READY"
        }
    },
    "externalService": {
        "type": "com.collective.celos.OozieExternalService",
        "properties": {
            "celos.oozie.url": "http://oj01.ny7.collective-media.net/oozie",
            "oozie.wf.application.path": "/deploy/dorado/workflow.xml",
            "jobTracker": "nn01.ny7.collective-media.net:8032",
            "nameNode": "hdfs://cluster-ny7"
        }
    },
    "maxRetryCount": 0
}
