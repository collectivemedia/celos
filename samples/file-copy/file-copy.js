addWorkflow({

    "id": "file-copy",

    "maxRetryCount": 0,

    "schedule": hourlySchedule(),

    "schedulingStrategy": serialSchedulingStrategy(),

    "trigger": hdfsCheckTrigger(
        "/user/celos/samples/file-copy/input/${year}-${month}-${day}T${hour}00.txt",
        "hdfs://nn"
    ),

    "externalService": oozieExternalService(
        {
            "user.name": "celos",
            "oozie.wf.application.path": "/user/celos/samples/file-copy/workflow/workflow.xml",
            "inputDir": "hdfs:/user/celos/samples/file-copy/input",
            "outputDir": "hdfs:/user/celos/samples/file-copy/output"
        },
        "http://nn:11000/oozie"
    )

});
