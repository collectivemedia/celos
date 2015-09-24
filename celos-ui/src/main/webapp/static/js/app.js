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
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] == variable) {
            return pair[1];
        }
    }
    return null;
}

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

var slotsNum = Math.trunc(($(window).width() - 250) / (30 + 4)) - 1;

var WorkflowsGroupFetch = React.createClass({
    displayName: "WorkflowsGroupFetch",

    loadCommentsFromServer: function loadCommentsFromServer(props) {
        console.log("loadCommentsFromServer:", props);
        $.ajax({
            url: "/react",
            data: {
                count: slotsNum,
                group: props.name,
                zoom: props.request.zoom,
                time: props.request.time
            },
            dataType: 'json',
            cache: false,
            success: (function (data) {
                this.setState({ data: data });
            }).bind(this),
            error: (function (xhr, status, err) {
                console.error(props.url, status, err.toString());
            }).bind(this)
        });
    },
    componentWillMount: function componentWillMount() {
        console.log("componentWillMount:", this.props);
        this.loadCommentsFromServer(this.props);
    },
    componentWillReceiveProps: function componentWillReceiveProps(nextProps) {
        console.log("componentWillReceiveProps:", nextProps);
        this.loadCommentsFromServer(nextProps);
    },
    render: function render() {
        console.log("WorkflowsGroupFetch", this.state);
        if (this.state) {
            return React.createElement(WorkflowsGroup, { data: this.state.data, request: this.props.request });
        } else {
            return React.DOM.div(null);
        }
    }
});

var WorkflowsGroup = React.createClass({
    displayName: "WorkflowsGroup",

    render: function render() {
        console.log("WorkflowsGroup", this.props.data);
        var req = this.props.request;
        var groupName = this.props.data.name;
        var newGroups;
        if (req.groups && req.groups != []) {
            newGroups = req.groups.filter(function (x) {
                return x != groupName;
            });
        } else {
            newGroups = [groupName];
        }
        var newUrl = makeCelosHref(req.zoom, req.time, newGroups);
        return React.DOM.table({ className: "workflowTable" },
            React.DOM.thead(null,
                React.DOM.tr(null,
                    React.DOM.th({ className: "groupName" },
                    React.DOM.a({ href: newUrl }, this.props.data.name)),
                    this.props.data.times
                        .slice(-slotsNum)
                        .map(function (tt, i) {
                            return React.DOM.th({ className: "timeHeader", key: i }, tt);
                        })
                )),
            React.DOM.tbody(null,
                this.props.data.rows
                    .map(function (product, key) {
                        return React.createElement(ProductRow, { data: product, key: key });
                    })
            ));
    }
});

var ProductRow = React.createClass({
    displayName: "ProductRow",

    render: function render() {
        return React.DOM.tr(null, React.DOM.th({ className: "workflowName" }, this.props.data.workflowName), this.props.data.slots.slice(-slotsNum).map(function (slot, i) {
            return React.createElement(TimeSlot, { data: slot, key: i });
        }));
    }
});

var TimeSlot = React.createClass({
    displayName: "TimeSlot",

    render: function render() {
        return React.DOM.td({ className: "slot " + this.props.data.status },
            React.DOM.a({ href: this.props.data.url },
                !this.props.data.quantity
                    ? React.DOM.div(null)
                    : React.DOM.div(null, this.props.data.quantity)
            )
        );
    }
});

console.log("WORKFLOW loaded");

var CelosMainFetch = React.createClass({
    displayName: "CelosMainFetch",

    getInitialState: function getInitialState() {
        return { data: { rows: [], navigation: {} } };
    },
    loadCommentsFromServer: function loadCommentsFromServer(props) {
        //        console.log("loadCommentsFromServer " + props.request.zoom + " " + props.request.time)
        $.ajax({
            url: props.url,
            data: {
                zoom: props.request.zoom,
                time: props.request.time
            },
            dataType: 'json',
            cache: false,
            success: (function (data) {
                this.setState({ data: data });
            }).bind(this),
            error: (function (xhr, status, err) {
                console.error(props.url, status, err.toString());
            }).bind(this)
        });
    },
    componentWillMount: function componentWillMount() {
        this.loadCommentsFromServer(this.props);
    },
    componentWillReceiveProps: function componentWillReceiveProps(nextProps) {
        this.loadCommentsFromServer(nextProps);
    },
    render: function render() {
        console.log("CelosMainFetch", this.props, this.state);
        var tmp = this.state.data;
        if (this.props.request.groups) {
            var groupFilter = this.props.request.groups;
            tmp.rows.forEach(function (x) {
                x.active = groupFilter.indexOf(x.name) >= 0;
            });
        } else {
            tmp.rows.forEach(function (x) {
                x.active = true;
            });
        }
        return React.createElement(CelosMain, { data: tmp, request: this.props.request });
    }
});

var CelosMain = React.createClass({
    displayName: "CelosMain",

    render: function render() {
        console.log("CelosMain", this.props.data, this.props.request);

        return React.DOM.div(null,
            React.createElement("h2", null, this.props.data.currentTime),
            React.createElement(Navigation, { data: this.props.data.navigation, request: this.props.request }),
            this.props.data.rows.map((function (wfGroup, i) {
                if (wfGroup.active) {
                    return React.DOM.div({ key: i },
                        React.createElement(WorkflowsGroupFetch, {
                            name: wfGroup.name,
                            active: wfGroup.active,
                            request: this.props.request
                        }),
                        React.DOM.br());
                } else {
                    var req = this.props.request;
                    var newUrl = makeCelosHref(req.zoom, req.time, req.groups.concat(wfGroup.name));
                    return React.DOM.div({ key: i },
                        React.DOM.a({ href: newUrl }, wfGroup.name)
                    );
                }
            }).bind(this))
        );
    }
});

var Navigation = React.createClass({
    displayName: "Navigation",

    render: function render() {
        console.log("Navigation", this.props.data);
        return React.createElement("center", { className: "bigButtons" },
            React.DOM.a({ href: makeCelosHref(this.props.request.zoom, this.props.data.left, this.props.request.groups) }, "< Prev page"),
            React.DOM.strong(null, " | "),
            React.DOM.a({ href: makeCelosHref(this.props.request.zoom, this.props.data.right, this.props.request.groups) }, "Next page >"),
            React.DOM.br(),
            React.DOM.br(),
            React.DOM.a({ href: makeCelosHref(this.props.data.zoomOut, this.props.request.time, this.props.request.groups) }, "Zoom OUT"),
            React.DOM.strong(null, " / ", this.props.request.zoom || 60, " minutes / "),
            React.DOM.a({ href: makeCelosHref(this.props.data.zoomIn, this.props.request.time, this.props.request.groups) }, "Zoom IN"),
            React.DOM.br(),
            React.DOM.br()
        );
    }
});

var defaultController = function defaultController() {
    if (window.location.hash === "" || window.location.hash === "#ui") {
        ReactDOM.render(React.createElement(CelosMainFetch, { url: "/react", request: {} }), document.getElementById('content'));
    } else if (startsWith("#ui?", window.location.hash)) {
        var params = parseParams(window.location.hash.substring("#ui?".length).split("&"));
        var request = { groups: params.groups, zoom: params.zoom, time: params.time };
        ReactDOM.render(React.createElement(CelosMainFetch, { url: "/react", request: request }), document.getElementById('content'));
    } else if (window.location.hash.indexOf("#test") === 0) {
        ReactDOM.render(React.createElement(CelosMainFetch, { url: "assets/main.json" }), document.getElementById('content'));
    } else {
        throw "no route for this URL: " + window.location.hash;
    }
};

window.addEventListener('hashchange', function () {
    console.log("URL:", window.location.hash);
    defaultController();
});

console.log("APP loaded");

defaultController();