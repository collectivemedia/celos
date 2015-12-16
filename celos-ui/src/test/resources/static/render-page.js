
"use strict";

var TEST_CONFIG = {
  "rows" : [ {
    "name" : "Flume",
    "rows" : [ ]
  }, {
    "name" : "Parquetify",
    "rows" : [ ]
  }, {
    "name" : "Unlisted workflows",
    "rows" : [ ]
  } ]
};

var ajaxGetJson = function(url0, data, successCallback) {

    console.log(url0);


    if (url0 === "/group") {
        return {};
    } else {
        throw new Error("dsadsads");
    }

}

var slotsNum = 42;

_internalSlotsData = Immutable.fromJS(TEST_CONFIG).set("navigation", Immutable.Map());
console.log("config loaded", _internalSlotsData.toJS());
var request = { groups: ["Flume"], zoom: undefined, time: undefined };
var result = ReactDOMServer.renderToStaticMarkup(
    React.createElement(CelosMainFetch, { url: "/main", request: request })
);

console.log(result);

//document.getElementById('content').innerHTML = result;


//jjs pre.js node_modules/js-promise/js-promise.js node_modules/react/dist/react.js node_modules/react-dom/dist/react-dom-server.js node_modules/immutable/dist/immutable.js node_modules/events/events.js js/lib.js js/Dispatcher.js node_modules/object-assign/index.js js/Nav.js js/stores/sidebarStore.js js/stores/slotsStore.js js/components/Sidebar.js js/components/ContextMenu.js js/components/ModalBox.js js/components/SlotsTable.js js/components/Main.js ololo.js


