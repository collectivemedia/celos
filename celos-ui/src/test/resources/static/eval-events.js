
"use strict";



    // don't use network in test mode
var ajaxGetJson = function(url0, data, successCallback) {
    console.log("ajaxGetJson fake" + url0);
};

var slotsNum = 42;

// TODO select some slot to check sidebar render

_internalSlotsData = Immutable.fromJS(MOCK_CONFIG);
_internalSlotsData = _internalSlotsData.set("navigation", Immutable.Map());
_internalSlotsData = _internalSlotsData.setIn(["rows", 0], Immutable.fromJS(MOCK_GROUP_FLUME));
_internalSlotsData = _internalSlotsData.setIn(["rows", 1], Immutable.fromJS(MOCK_GROUP_PARQUETIFY));

console.log("config loaded", _internalSlotsData.toJS());
var request = { groups: ["Flume", "Parquetify"], zoom: undefined, time: undefined };

AppDispatcher.clearSelection();


console.log(JSON.stringify(_internalSlotsData.toJS(), null, 2));

//document.getElementById('content').innerHTML = result;

