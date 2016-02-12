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

var console = console || {
    log: function (x, y) {
        java.lang.System.out.println("" + x + y)
    }
};

var ajaxGetJson = function(url0, data, successCallback) {
    // TODO write a test
    var _ajaxErrorCallback = function (xhr, status, err) {
        throw "ajaxGetJson: " + url0 + " " + status + " " + err;
    };
    var request = new XMLHttpRequest();
    var query = [];
    for (var key in data) {
        if (data.hasOwnProperty(key) && data[key] !== undefined) {
            var elem = data[key];
            if (typeof(ss) == "object" && "forEach" in elem) {
                elem.forEach(function(value) {
                    query.push(encodeURIComponent(key) + '=' + encodeURIComponent(value));
                })
            } else {
                query.push(encodeURIComponent(key) + '=' + encodeURIComponent(elem));
            }
        }
    }
    var urlQuery = url0 + (query.length ? '?' + query.join('&') : '');
    request.open('GET', urlQuery, true);
    request.onreadystatechange = function () {
        if (this.readyState == XMLHttpRequest.DONE) {
            if (this.status >= 200 && this.status < 400) {
                successCallback(JSON.parse(this.responseText))
            } else {
                _ajaxErrorCallback(this, this.status, this.statusText);
            }
        }
    };
    request.onerror = _ajaxErrorCallback;
    request.send();
};

var ajaxPostJSON = function (url0, jsondata, successCallback) {
    // TODO write a test
    var _ajaxErrorCallback = function (xhr, status, err) {
        throw "ajaxPostJSON: " + url0 + " " + status + " " + err;
    };
    var request = new XMLHttpRequest();
    request.open("POST", url0);
    request.setRequestHeader('Content-Type', 'application/json');
    request.onreadystatechange = function () {
        if (this.readyState == XMLHttpRequest.DONE) {
            if (this.status >= 200 && this.status < 400) {
                successCallback(JSON.parse(this.responseText))
            } else {
                _ajaxErrorCallback(this, this.status, this.statusText);
            }
        }
    };
    request.onerror = _ajaxErrorCallback;
    request.send(JSON.stringify(jsondata));
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
            throw "Empty parameter";
        }
        var tmp = parameter.split("=");
        var key = tmp[0];
        var value = tmp[1];
        if (key == "groups") {
            res["groups"] = value.split(",")
                .map(decodeURIComponent)
                .filter(function (x) {
                    return x != "";
                });
        } else if (key == "zoom") {
            res["zoom"] = decodeURIComponent(value);
        } else if (key == "time") {
            res["time"] = decodeURIComponent(value);
        } else {
            throw "Unknown parameter: " + parameter;
        }
    });
    return res;
};

var addOrRemoveClass = function(elem, className) {
    if (elem.className.indexOf(className) == -1) {
        elem.className += " " + className;
    } else {
        elem.className = elem.className.replace(" " + className, "");
    }
};


