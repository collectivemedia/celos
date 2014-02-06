{
    "id": "wordcount",
    "schedule": {
        "type": "com.collective.celos.HourlySchedule"
    },
    "schedulingStrategy": {
        "type": "com.collective.celos.SerialSchedulingStrategy"
    },
    "trigger": {
        "type": "com.collective.celos.HDFSCheckTrigger",
        "properties": {
            "celos.hdfs.fs": "hdfs://nn",
            "celos.hdfs.path": "/user/celos/samples/wordcount/input/${year}-${month}-${day}T${hour}00.txt"
        }
    },
    "externalService": {
        "type": "com.collective.celos.OozieExternalService",
        "properties": {
            "nameNode": "hdfs://nn",
            "jobTracker": "hdfs://nn",
            "celos.oozie.url": "http://nn:11000/oozie",
            "user.name": "celos",
            "oozie.wf.application.path": "/user/celos/samples/wordcount/workflow/workflow.xml",
            "inputDir": "/user/celos/samples/wordcount/input",
            "outputDir": "/user/celos/samples/wordcount/output"
        }
    },
    "maxRetryCount": 0
}
