/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

"use strict";

// TODO extract constants from expression
var slotsNum = Math.trunc((window.innerWidth * 0.7 - 250) / (30 + 4)) - 1;

var defaultController = function () {
    if (window.location.hash === "" || window.location.hash === "#ui") {
        ReactDOM.render(React.createElement(CelosMainFetch, { url: "/main", request: {} }), document.getElementById('content'));
    } else if (startsWith("#ui?", window.location.hash)) {
        var params = parseParams(window.location.hash.substring("#ui?".length).split("&"));
        var request = { groups: params.groups, zoom: params.zoom, time: params.time };
        ReactDOM.render(React.createElement(CelosMainFetch, { url: "/main", request: request }), document.getElementById('content'));
    } else {
        throw "no route for this URL: " + window.location.hash;
    }
};

window.addEventListener('hashchange', function () {
    console.log("URL:", window.location.hash);
    defaultController();
});

// application entry point
ajaxGetJson(
    /*url=*/ "/groups",
    /*data=*/ {
    },
    /*success=*/ (function (data) {
        // deep merge works fine with empty lists
        _internalSlotsData = Immutable.fromJS(data).set("navigation", Immutable.Map());
        defaultController();
    })
);

window.onclick = function(e) {
    // close context menu
    ReactDOM.render(React.createElement(ContextMenu, {showElement: false}),
        document.getElementById('contextMenu'));
    return true;
};
