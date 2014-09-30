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
