var CELOS_USER = "celos";

var CELOS_DEFAULT_OOZIE = "http://oozie002.ny7.collective-media.net:11000/oozie";

var CELOS_DEFAULT_HDFS = "hdfs://nameservice1";

var NAME_NODE = CELOS_DEFAULT_HDFS;

var JOB_TRACKER = "cldmgr001.ewr004.collective-media.net:8032";

var HIVE_METASTORE = "thrift://hive002.ny7.collective-media.net:9083";

var ETL002 = "etl002.ny7.collective-media.net";

var OUTPUT_ROOT = NAME_NODE + "/output";

var CELOS_DEFAULT_OOZIE_PROPERTIES = {
    "user.name": CELOS_USER,
    "jobTracker" : JOB_TRACKER,
    "nameNode" : CELOS_DEFAULT_HDFS,
    "hiveMetastore": HIVE_METASTORE,
    "hiveDefaults": NAME_NODE + "/deploy/TheOneWorkflow/hive/hive-site.xml",
    "oozie.use.system.libpath": "true",
    "outputRoot": OUTPUT_ROOT,
    "etl002": ETL002
};
