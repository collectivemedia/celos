importDefaults("collective");

function myOozieProps(slotId) {
    return {
        "oozie.wf.application.path": hdfsPath("/user/celos/app/wordcount/workflow.xml"),
        "inputDir": hdfsPath("/user/celos/app/wordcount/input"),
        "outputDir": hdfsPath("/user/celos/app/wordcount/output")
    }
}

addWorkflow({
    "id": "wordcount",
    "maxRetryCount": 0,
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger(hdfsPath("/user/celos/app/wordcount/input/${year}-${month}-${day}T${hour}00.txt")),
    "externalService": oozieExternalService(myOozieProps)

});
