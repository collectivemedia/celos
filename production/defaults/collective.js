var CELOS_USER = "celos";

var CELOS_DEFAULT_OOZIE = "http://oozie002.ny7.collective-media.net:11000/oozie";

var CELOS_DEFAULT_HDFS = "hdfs://nameservice1";

var NAME_NODE = CELOS_DEFAULT_HDFS;

var JOB_TRACKER = "admin1.ny7.collective-media.net:8032";

var HIVE_METASTORE = "thrift://hive-meta.ny7.collective-media.net:9803";

var CELOS_DEFAULT_OOZIE_PROPERTIES = {
    "user.name": CELOS_USER,
    "jobTracker" : JOB_TRACKER,
    "nameNode" : CELOS_DEFAULT_HDFS,
    "hiveMetastore": HIVE_METASTORE,
    "oozie.use.system.libpath": "true"
};
