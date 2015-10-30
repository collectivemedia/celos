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

var slotsNum = Math.trunc((window.innerWidth * 0.7 - 250) / (30 + 4)) - 1;



var Navigation = React.createClass({
    displayName: "Navigation",

    render: function render() {
        console.log("DEBUG");
        console.log(this.props.data.toJS());
        var serverInfo = this.props.data.toJS();
        return React.createElement("center", { className: "bigButtons" },
            React.DOM.a({ href: makeCelosHref(this.props.request.zoom, serverInfo.left, this.props.request.groups) }, "< Prev page"),
            React.DOM.strong(null, " | "),
            React.DOM.a({ href: makeCelosHref(this.props.request.zoom, serverInfo.right, this.props.request.groups) }, "Next page >"),
            React.DOM.br(),
            React.DOM.br(),
            React.DOM.a({ href: makeCelosHref(serverInfo.zoomOut, this.props.request.time, this.props.request.groups) }, "- Zoom OUT"),
            React.DOM.strong(null, " / ", this.props.request.zoom || 60, " minutes / "),
            React.DOM.a({ href: makeCelosHref(serverInfo.zoomIn, this.props.request.time, this.props.request.groups) }, "Zoom IN +"),
            React.DOM.br(),
            React.DOM.br()
        );
    }
});

var defaultController = function defaultController() {
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



ajaxGetJson(
    /*url=*/ "/config",
    /*data=*/ {
    },
    /*success=*/ (function (data) {
        // deep merge works fine with empty lists
        _internalSlotsData = Immutable.fromJS(data).set("navigation", Immutable.Map());
        console.log("config loaded", _internalSlotsData.toJS());
        defaultController();
    })
);


var getNavigation = function (zoomStr, timeStr) {

    var ZOOM_LEVEL_MINUTES = [1, 5, 15, 30, 60, 60*24];
    var DEFAULT_ZOOM_LEVEL_MINUTES = 60;
    var MIN_ZOOM_LEVEL_MINUTES = 1;
    var MAX_ZOOM_LEVEL_MINUTES = 60*24; // Code won't work with higher level, because of toFullDay()

    var getZoomLevel = function () {
        if (zoomStr == null || zoomStr == "") {
            return DEFAULT_ZOOM_LEVEL_MINUTES;
        } else {
            var zoom = Number.parseInt(zoomStr);
            if (zoom < MIN_ZOOM_LEVEL_MINUTES) {
                return MIN_ZOOM_LEVEL_MINUTES;
            } else if (zoom > MAX_ZOOM_LEVEL_MINUTES) {
                return MAX_ZOOM_LEVEL_MINUTES;
            } else {
                return zoom;
            }
        }
    };
    var zoom = getZoomLevel(zoomStr);
    var timeShift;
    if (timeStr == null || timeStr == "" || timeStr == undefined) {
        timeShift = new Date()
    } else {
        timeShift = new Date(timeStr)
    }

    var minusMinutes = function(time, m) {
        var tmp = new Date(time);
        tmp.setHours(time.getMinutes() + m);
        return tmp
    };

    var plusMinutes = function(time, m) {
        var tmp = new Date(time);
        tmp.setHours(time.getMinutes() - m);
        return tmp
    };

    var PAGE_SIZE = 20;
    var result = {};
    var now = new Date();
    result.left = minusMinutes(timeShift, PAGE_SIZE * zoom).toISOString();
    // right link
    var tmp = plusMinutes(timeShift, PAGE_SIZE * zoom);
    if (tmp >= now) {
        result.right = null;
    } else {
        result.right = tmp.toISOString();
    }
    // makeZoomButtons
    var last = ZOOM_LEVEL_MINUTES.length - 1;
    var pos = ZOOM_LEVEL_MINUTES.indexOf(zoom);
    // FIXME
    if (pos == -1) {
        alert("DEBUG MOAFONASONFASONFASOF")
    }
    result.zoomIn = (0 < pos && pos <= last) ? ZOOM_LEVEL_MINUTES[pos - 1] : ZOOM_LEVEL_MINUTES[0];
    result.zoomOut = (0 <= pos && pos < last) ? ZOOM_LEVEL_MINUTES[pos + 1] : ZOOM_LEVEL_MINUTES[last];
    result.currentTime = new Date().toISOString();
    return result;
};






