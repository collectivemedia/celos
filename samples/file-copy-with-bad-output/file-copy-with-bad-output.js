importDefaults("collective");

celos.defineWorkflow({

    "id": "file-copy-with-bad-output",

    "maxRetryCount": 0,

    "schedule": celos.hourlySchedule(),

    "schedulingStrategy": celos.serialSchedulingStrategy(),

    "trigger": celos.hdfsCheckTrigger(
        "/user/celos/samples/file-copy-with-bad-output/input/${year}-${month}-${day}T${hour}00.txt"
    ),

    "externalService": celos.oozieExternalService(
        {
            "user.name": "celos",
            "oozie.wf.application.path": "/user/celos/samples/file-copy-with-bad-output/workflow/workflow.xml",
            "inputDir": "hdfs:/user/celos/samples/file-copy-with-bad-output/input",
            "outputDir": "hdfs:/user/celos/samples/file-copy-with-bad-output/output"
        }
    )

});
