importDefaults("collective");

addWorkflow({
    "id": "wordcount",
    "schedule": hourlySchedule(),
    "schedulingStrategy": serialSchedulingStrategy(),
    "trigger": hdfsCheckTrigger(hdfsPath("/input/wordcount/${year}-${month}-${day}-${hour}00/_SUCCESS")),
    "externalService": oozieExternalService({
        "oozie.wf.application.path": hdfsPath("/user/celos/app/wordcount/workflow.xml"),
        "oozie_wf_base_path": hdfsPath("/user/celos/app/wordcount"),
        "db_name": databaseName("celosdb")
    })
});
