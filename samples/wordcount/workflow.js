importDefaults("collective");
addWorkflow({
    "id": "wordcount",
    "maxRetryCount": 0,
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger("/user/akonopko/app/wordcount/input/${year}-${month}-${day}T${hour}00.txt"),
    "externalService": oozieExternalService(
        {
            "oozie.wf.application.path": "/user/akonopko/app/wordcount/workflow/workflow.xml",
            "inputDir": "/user/akonopko/app/wordcount/input",
            "outputDir": "/user/akonopko/app/wordcount/output"
        }
    )

});
