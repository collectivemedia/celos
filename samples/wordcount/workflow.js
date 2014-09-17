importDefaults("collective");

function myOozieProps(slotId) {
    return {
        "oozie.wf.application.path": hdfsPath("/user/akonopko/app/wordcount/workflow/workflow.xml"),
        "inputDir": hdfsPath("/user/akonopko/app/wordcount/input"),
        "outputDir": hdfsPath("/user/akonopko/app/wordcount/output")
    }
}

addWorkflow({
    "id": "wordcount",
    "maxRetryCount": 0,
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger("/user/akonopko/app/wordcount/input/${year}-${month}-${day}T${hour}00.txt"),
    "externalService": oozieExternalService(myOozieProps)

});
