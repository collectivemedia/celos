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
var CELOS_DEFAULT_OOZIE = "http://oozie:11000/oozie";
var CELOS_DEFAULT_HDFS = "file://";
var NAME_NODE = CELOS_DEFAULT_HDFS;
var JOB_TRACKER = "jobtracker:8032";
var HIVE_METASTORE = "thrift://hive:9083";

var CELOS_DEFAULT_OOZIE_PROPERTIES = {
    "user.name": "default",
    "jobTracker" : "JOB_TRACKER",
    "nameNode" : "CELOS_DEFAULT_HDFS",
    "hiveMetastore": "HIVE_METASTORE",
    "hiveDefaults": "/hive/hive-site.xml",
    "oozie.use.system.libpath": "true"
};
