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

var startsWith = function startsWith(searchString, str) {
    return str.indexOf(searchString) === 0;
};

function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var elem in data) {
        var pair = elem.split("=");
        if (pair[0] == variable) {
            return pair[1];
        }
    }
    return null;
}


var ajaxGetJson = function(url0, data, successCallback, errorCallback) {
    var request = new XMLHttpRequest();
    var query = [];
    for (var key in data) {
        if (data.hasOwnProperty(key) && data[key] != undefined) {
            query.push(encodeURIComponent(key) + '=' + encodeURIComponent(data[key]));
        }
    }
    var urlQuery = url0 + (query.length ? '?' + query.join('&') : '');
    request.open('GET', urlQuery, true);
    request.onreadystatechange = function() {
        if (this.readyState == XMLHttpRequest.DONE) {
            if (this.status >= 200 && this.status < 400) {
                // Success!
                successCallback(JSON.parse(this.responseText))
            } else {
                // We reached our target server, but it returned an error
                errorCallback(this, this.status, this.statusText);
            }
        }
    };
    request.onerror = errorCallback;
    request.send();
};

var makeCelosHref = function makeCelosHref(zoom, time, groups) {
    var url0 = "#ui?";
    if (zoom) {
        url0 += "zoom=" + encodeURIComponent(zoom) + "&";
    }
    if (time) {
        url0 += "time=" + encodeURIComponent(time) + "&";
    }
    if (groups && groups.length != 0) {
        url0 += "groups=" + groups.map(encodeURIComponent).join(",") + "&";
    }
    return url0.substring(0, url0.length - 1);
};

var parseParams = function parseParams(paramsList) {
    var res = {};
    paramsList.forEach(function (parameter) {
        if (parameter === "") {
            // pass
        } else if (startsWith("groups=", parameter)) {
            res["groups"] = parameter.substring("groups=".length).split(",").map(decodeURIComponent)
                .filter(function (x) {
                    return x;
                });
        } else if (startsWith("zoom=", parameter)) {
            res["zoom"] = decodeURIComponent(parameter.substring("zoom=".length));
        } else if (startsWith("time=", parameter)) {
            res["time"] = decodeURIComponent(parameter.substring("time=".length));
        } else {
            throw "Unknown parameter: " + parameter;
        }
    });
    return res;
};

console.log("LIB loaded");
