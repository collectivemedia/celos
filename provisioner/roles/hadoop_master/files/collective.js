var CELOS_DEFAULT_OOZIE = "http://nn:11000/oozie";
var CELOS_DEFAULT_HDFS = "hdfs://nn";
var NAME_NODE = CELOS_DEFAULT_HDFS;
var JOB_TRACKER = "nn:8032";
var OUTPUT_ROOT = NAME_NODE + "/output";
var CELOS_USER = "celos";

var CELOS_DEFAULT_OOZIE_PROPERTIES = {
    "user.name": CELOS_USER,
    "jobTracker" : JOB_TRACKER,
    "nameNode" : CELOS_DEFAULT_HDFS,
    "oozie.use.system.libpath": "true",
    "outputRoot": OUTPUT_ROOT
};
