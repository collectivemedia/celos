ci.addTestCase({
    name: "filecopy test case 1",
    sampleTimeStart: "2013-12-20T16:00Z",
    sampleTimeEnd: "2013-12-20T18:00Z",
    inputs: [
        ci.hdfsInput(ci.fixDirFromResource("test-1/input"), "/input/file-copy")
    ],
    outputs: [
        ci.plainCompare(ci.fixDirFromResource("test-1/output"), "/output/file-copy")
    ]
});