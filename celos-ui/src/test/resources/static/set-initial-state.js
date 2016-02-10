"use strict";

// don't use network in test mode
var ajaxGetJson = function(url0, data, successCallback) {
    console.log("ajaxGetJson" + url0);
};

var slotsNum = 42;

_internalSlotsData = Immutable.fromJS(MOCK_CONFIG);
_internalSlotsData = _internalSlotsData.set("navigation", Immutable.Map());
_internalSlotsData = _internalSlotsData.setIn(["rows", 0], Immutable.fromJS(MOCK_GROUP_FLUME));
_internalSlotsData = _internalSlotsData.setIn(["rows", 1], Immutable.fromJS(MOCK_GROUP_PARQUETIFY));

console.log("config loaded", _internalSlotsData.toJS());
var request = { groups: ["Flume", "Parquetify"], zoom: undefined, time: undefined };

