addTestCase({
    name: "wordcount test case 1",
    sampleTimeStart: "2013-12-20T16:00Z",
    sampleTimeEnd: "2013-12-20T18:00Z",
    inputs: [
        hdfsInput(fixDirFromResource("test-1/input/plain/input/wordcount"), "input/wordcount")
    ],
    outputs: [
        hdfsOutput(fixDirFromResource("test-1/output/plain/output/wordcount"), "output/wordcount")
    ]
});