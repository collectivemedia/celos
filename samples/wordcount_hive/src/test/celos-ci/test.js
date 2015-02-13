importPackage(Packages.com.collective.celos.ci.testing.fixtures.compare);

var tableScript = fixFile("CREATE TABLE wordcount PARTITIONED BY (year INT)" +
                        "  ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe'" +
                        "  WITH SERDEPROPERTIES ('avro.schema.url'='${SANDBOX}/schema/wordcount.avsc')" +
                        "  STORED as INPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat'" +
                        "  OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat'");

var resTableScript = fixFile("CREATE TABLE result (word string, number int)");

addTestCase({
    name: "Hive wordcount test case 1",
    sampleTimeStart: "2013-12-20T16:00Z",
    sampleTimeEnd: "2013-12-20T16:00Z",
    inputs: [
        hdfsInput(fixDirFromResource("test-1/input/wordcount"), "input/wordcount"),
        hdfsInput(fixDirFromResource("test-1/schema"), "/schema"),
        hiveInput("celosdb", "wordcount", tableScript, fixTableFromResource("test-1/input/tsv/wordcount.tsv")),
        hiveInput("celosdb", "result", resTableScript)
    ],
    outputs: [
        ci.fixTableCompare(
            ci.fixTableFromResource("test-1/output/tsv/result.tsv"),
            ci.hiveTable("celosdb", "result")
        )
    ]
});
