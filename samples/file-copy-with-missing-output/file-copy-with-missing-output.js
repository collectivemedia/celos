addWorkflow({
    "id": "file-copy-with-missing-output",
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
            "celos.hdfs.path": "/user/celos/samples/file-copy-with-missing-output/input/${year}-${month}-${day}T${hour}00.txt"
        }
    },
    "externalService": {
        "type": "com.collective.celos.OozieExternalService",
        "properties": {
            "celos.oozie.url": "http://nn:11000/oozie",
            "user.name": "celos",
            "oozie.wf.application.path": "/user/celos/samples/file-copy-with-missing-output/workflow/workflow.xml",
            "inputDir": "hdfs:/user/celos/samples/file-copy-with-missing-output/input",
            "outputDir": "hdfs:/user/celos/samples/file-copy-with-missing-output/output"
        }
    },
    "maxRetryCount": 0
});
