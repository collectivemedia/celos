define(function () {

    var SECOND_MS = 1000;
    var MINUTE_MS = 60 * SECOND_MS;
    var HOUR_MS = 60 * MINUTE_MS;

    return {
        SECOND_MS: SECOND_MS,
        MINUTE_MS: MINUTE_MS,
        HOUR_MS: HOUR_MS,
        DAY_MS: 24 * HOUR_MS,
        SAMPLE_DATE: '2015-02-05 13:23 UTC',
        SAMPLE_DATE_DAY: '2015-02-05',
        SAMPLE_LINE_TIME: '13:23',
        REFRESH_INTERVAL: HOUR_MS,
        ZOOM_TOO_LOW_STROKE_MARGIN: 5,
        ZOOM_TOO_LOW_STROKE_STEP: 10,
        workflowNameMargin: 20,
        workflowNameYOffset: 8,
        workflowGroupNameFontSize: 18,
        workflowNameFontSize: 15,
        slotStateAbbreviationFontSize: 15,
        dateTextFontSize: 17,
        baseCellWidth: 69,
        cellHeight: 36,
        scrollbarWidth: 20,
        cellMinimalWidth: 5,
        cellColorZoomMe: '#bbb',
        zoomInDesrcText: 'Zoom in to see Slots'
    }

});