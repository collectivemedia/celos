celos.importDefaults("example-settings");

function inputPathForDataCenter(dc) {
    return "/user/" + CELOS_USER + "/celos/quickstart/input/" + dc + "/${year}-${month}-${day}/${hour}00";
}

function outputPathForDataCenter(dc) {
    return "/user/" + CELOS_USER + "/celos/quickstart/output/" + dc + "/${year}-${month}-${day}/${hour}00";
}

function defineWordCountWorkflow(dc) {
    var inputPath = inputPathForDataCenter(dc);
    var outputPath = outputPathForDataCenter(dc);
    celos.defineWorkflow({
        "id": "wordcount-" + dc,
        "schedule": celos.hourlySchedule(),
        "schedulingStrategy": celos.serialSchedulingStrategy(),
        "trigger": celos.hdfsCheckTrigger(inputPath + "/_READY"),
        "externalService": celos.oozieExternalService({
            "oozie.wf.application.path": "/user/" + CELOS_USER + "/celos/quickstart/app/workflow.xml",
            "inputPath": inputPath,
            "outputPath": outputPath
        })
    });
}

defineWordCountWorkflow("nyc");
defineWordCountWorkflow("lax");
