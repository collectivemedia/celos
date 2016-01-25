
"use strict";


// don't use network in test mode
var ajaxGetJson = function(url0, data, successCallback) {
    console.log("ajaxGetJson" + url0);
};

var slotsNum = 42;

// TODO select some slot to check sidebar render

_internalSlotsData = Immutable.fromJS(MOCK_CONFIG);
_internalSlotsData = _internalSlotsData.set("navigation", Immutable.Map());
_internalSlotsData = _internalSlotsData.setIn(["rows", 0], Immutable.fromJS(MOCK_GROUP_FLUME));
_internalSlotsData = _internalSlotsData.setIn(["rows", 1], Immutable.fromJS(MOCK_GROUP_PARQUETIFY));

console.log("config loaded", _internalSlotsData.toJS());
var request = { groups: ["Flume", "Parquetify"], zoom: undefined, time: undefined };

//var tmp = React.createElement(CelosMainFetch, { url: "/main", request: request });
//var result = ReactDOMServer.renderToString(tmp);

//console.log(result);

//console.log(JSON.stringify(_internalSlotsData.toJS()));

//document.getElementById('content').innerHTML = result;

