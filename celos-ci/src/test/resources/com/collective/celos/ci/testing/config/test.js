addTestCase({
    name: "wordcount test case 1",
    sampleTimeStart: "2013-11-20T11:00Z",
    sampleTimeEnd: "2013-11-20T18:00Z",
    inputs: [
        hdfsInput(fixDirFromResource("src/test/celos-ci/test-1/input/plain/input/wordcount1"), "input/wordcount1"),
        hdfsInput(fixDirFromResource("src/test/celos-ci/test-1/input/plain/input/wordcount11"), "input/wordcount11")
    ],
    outputs: [
        plainCompare(fixDirFromResource("src/test/celos-ci/test-1/output/plain/output/wordcount1"), "output/wordcount1")
    ]
})

addTestCase({
    name: "wordcount test case 2",
    sampleTimeStart: "2013-12-20T16:00Z",
    sampleTimeEnd: "2013-12-20T18:00Z",
    inputs: [
        hdfsInput(fixDirFromResource("src/test/celos-ci/test-1/input/plain/input/wordcount2"), "input/wordcount2")
    ],
    outputs: [
        plainCompare(fixDirFromResource("src/test/celos-ci/test-1/output/plain/output/wordcount2"), "output/wordcount2")
    ]
});