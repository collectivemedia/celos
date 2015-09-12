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
importDefaults("collective");

celos.defineWorkflow({
    "id": "file-copy",
    "maxRetryCount": 0,
    "schedule": celos.hourlySchedule(),
    "schedulingStrategy": celos.serialSchedulingStrategy(),
    "trigger": celos.hdfsCheckTrigger("/user/celos-ci/celos.selftest/input/file-copy/${year}-${month}-${day}T${hour}00.txt"),
    "externalService": celos.oozieExternalService({
        "oozie.wf.application.path": "/user/celos-ci/celos.selftest/user/celos/app/file-copy/workflow.xml",
        "inputDir": "hdfs:/user/celos-ci/celos.selftest/input/file-copy",
        "outputDir": "hdfs:/user/celos-ci/celos.selftest/output/file-copy"
    })

});
