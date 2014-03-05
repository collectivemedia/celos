addWorkflow({

    "id": "wordcount",

    "maxRetryCount": 0,

    "schedule": hourlySchedule(),

    "schedulingStrategy": serialSchedulingStrategy(),

    "trigger": hdfsCheckTrigger(
        "/user/celos/samples/wordcount/input/${year}-${month}-${day}T${hour}00.txt",
        "hdfs://nn"
    ),

    "externalService": oozieExternalService(
        {
            "nameNode": "hdfs://nn",
            "jobTracker": "hdfs://nn",
            "user.name": "celos",
            "oozie.wf.application.path": "/user/celos/samples/wordcount/workflow/workflow.xml",
            "inputDir": "/user/celos/samples/wordcount/input",
            "outputDir": "/user/celos/samples/wordcount/output"
        },
        "http://nn:11000/oozie"
    )

});
