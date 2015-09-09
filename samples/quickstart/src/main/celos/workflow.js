celos.importDefaults("example-settings");

var ROOT = "/user/" + CELOS_USER + "/celos/quickstart";

function inputPathForDataCenter(dc) {
    return ROOT + "/input/" + dc + "/${year}-${month}-${day}/${hour}00";
}

function outputPathForDataCenter(dc) {
    return ROOT + "/output/" + dc + "/${year}-${month}-${day}/${hour}00";
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
            "oozie.wf.application.path": ROOT + "/app/workflow.xml",
            "inputPath": inputPath,
            "outputPath": outputPath
        })
    });
}

defineWordCountWorkflow("lax");
defineWordCountWorkflow("nyc");

