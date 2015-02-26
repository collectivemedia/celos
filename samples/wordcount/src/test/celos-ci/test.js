ci.addTestCase({
    name: "wordcount test case 1",
    sampleTimeStart: "2013-12-20T16:00Z",
    sampleTimeEnd: "2013-12-20T17:00Z",
    inputs: [
        ci.hdfsInput(ci.fixDirFromResource("test-1/input"), "/input/wordcount")
    ],
    outputs: [
        ci.plainCompare(ci.fixDirFromResource("test-1/output"), "/output/wordcount")
    ]
});

ci.addTestCase({
    name: "wordcount test case 2",
    sampleTimeStart: "2013-12-20T18:00Z",
    sampleTimeEnd: "2013-12-20T18:00Z",
    inputs: [
        ci.hdfsInput(ci.fixDirFromResource("test-2/input/plain/input/wordcount"), "input/wordcount")
    ],
    outputs: [
        ci.plainCompare(ci.fixDirFromResource("test-2/output/plain/output/wordcount"), "output/wordcount")
    ]
});
