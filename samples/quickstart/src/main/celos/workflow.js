/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
celos.importDefaults("wordcount");

var ROOT = "/user/" + CELOS_USER + "/celos/quickstart";

function inputPathForDataCenter(dc) {
    return ROOT + "/input/" + dc + "/${year}-${month}-${day}/${hour}00";
}

function outputPathForDataCenter(dc) {
    return ROOT + "/output/" + dc + "/${year}-${month}-${day}/${hour}00";
}

/*
 * This function defines a wordcount workflow for the given data
 * center.
 */
function defineWordCountWorkflow(dc) {
    var inputPath = inputPathForDataCenter(dc);
    var outputPath = outputPathForDataCenter(dc);
    celos.defineWorkflow({
        "id": "wordcount-" + dc,
        "schedule": celos.hourlySchedule(),
        "schedulingStrategy": celos.serialSchedulingStrategy(),
        "trigger": celos.hdfsCheckTrigger(inputPath + "/_READY"),
        "externalService": celos.oozieExternalService({
            // These properties are passed to the Oozie job
            "oozie.wf.application.path": ROOT + "/wordcount/workflow.xml",
            "inputPath": inputPath,
            "outputPath": outputPath
        })
    });
}

/*
 * Set up two workflows, one for each data center.
 */
defineWordCountWorkflow("lax");
defineWordCountWorkflow("nyc");

