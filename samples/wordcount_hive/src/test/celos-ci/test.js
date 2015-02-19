var tableScript = ci.fixFile("CREATE TABLE input PARTITIONED BY (year INT)" +
                          "  ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe'" +
                          "  WITH SERDEPROPERTIES ('avro.schema.url'='${SANDBOX}/schema/wordcount.avsc')" +
                          "  STORED as INPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat'" +
                          "  OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat'");

var resTableScript = ci.fixFile("CREATE TABLE result (word string, number int)");

ci.addTestCase({
    name: "Hive wordcount test case 1",
    sampleTimeStart: "2013-12-20T16:00Z",
    sampleTimeEnd: "2013-12-20T16:00Z",
    inputs: [
        ci.hdfsInput(ci.fixDirFromResource("test-1/schema"), "/schema"),
        ci.hiveInput("wordcountdb", "input", tableScript, ci.fixTableFromResource("test-1/input.tsv")),
        ci.hiveInput("wordcountdb", "result", resTableScript)
    ],
    outputs: [
        ci.fixTableCompare(
            ci.fixTableFromResource("test-1/result.tsv"),
            ci.hiveTable("wordcountdb", "result")
        )
    ]
});
