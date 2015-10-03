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

var slotsNum = Math.trunc((window.innerWidth - 250) / (30 + 4)) - 1;

var WorkflowsGroupFetch = React.createClass({
    displayName: "WorkflowsGroupFetch",

    loadCommentsFromServer: function loadCommentsFromServer(props) {
        console.log("WorkflowsGroupFetch fromServer:", props);
        ajaxGetJson(
            /*url=*/ "/group",
            /*data=*/ {
                count: slotsNum,
                group: props.name,
                zoom: props.request.zoom,
                time: props.request.time
            },
            /*success=*/ function (data) {
                this.setState({ data: data });
            }.bind(this),
            /*error=*/ function (xhr, status, err) {
                console.error(props.url, status, err.toString());
            }.bind(this)
        );
    },
    componentWillMount: function () {
        this.loadCommentsFromServer(this.props);
    },
    componentWillReceiveProps: function (nextProps) {
        this.loadCommentsFromServer(nextProps);
    },
    render: function render() {
        if (this.state) {
            return React.createElement(WorkflowsGroup, { data: this.state.data, request: this.props.request });
        } else {
            return null
        }
    }
});

var CelosMainFetch = React.createClass({
    displayName: "CelosMainFetch",

    getInitialState: function () {
        return { data: { rows: [], navigation: {} } };
    },
    loadCommentsFromServer: function loadCommentsFromServer(props) {
        ajaxGetJson(
            /*url=*/ props.url,
            /*data=*/ {
                zoom: props.request.zoom,
                time: props.request.time
            },
            /*success=*/ (function (data) {
                this.setState({ data: data });
            }).bind(this),
            /*error=*/ (function (xhr, status, err) {
                console.error(props.url, status, err.toString());
            }).bind(this)
        );
    },
    componentWillMount: function componentWillMount() {
        this.loadCommentsFromServer(this.props);
    },
    componentWillReceiveProps: function componentWillReceiveProps(nextProps) {
        this.loadCommentsFromServer(nextProps);
    },
    render: function render() {
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

    render: function () {
        console.log("CelosMain", this.props);
        return React.DOM.div(null,
            React.createElement(ContextMenu, {}),
            React.createElement("h2", null, this.props.data.currentTime),
            React.createElement(Navigation, { data: this.props.data.navigation, request: this.props.request }),
            this.props.data.rows.map(function (wfGroup, i) {
                if (wfGroup.active) {
                    return React.DOM.div({ key: i },
                        React.createElement(WorkflowsGroupFetch, {
                            name: wfGroup.name,
                            active: wfGroup.active,
                            request: this.props.request
                        }),
                        React.DOM.br())
                } else {
                    var req = this.props.request;
                    var newUrl = makeCelosHref(req.zoom, req.time, req.groups.concat(wfGroup.name));
                    return React.DOM.div({ key: i },
                        React.DOM.a({ href: newUrl }, wfGroup.name)
                    )
                }
            }.bind(this))
        );
    }
});

var ContextMenu = React.createClass({
    displayName: "ContextMenu",

    render: function () {
        if (!this.props.showElement) {
            return null
        }
        // else
        return (
            React.DOM.ul({className: "context-menu",
                          style: {top: this.props.y,
                                  left: this.props.x}},
                React.DOM.li(null, "First thing"),
                React.DOM.li(null, "Second thing"),
                React.DOM.li(null, "Third thing")))
    }
});

var Navigation = React.createClass({
    displayName: "Navigation",

    render: function render() {
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
        ReactDOM.render(React.createElement(CelosMainFetch, { url: "/main", request: {} }), document.getElementById('content'));
    } else if (startsWith("#ui?", window.location.hash)) {
        var params = parseParams(window.location.hash.substring("#ui?".length).split("&"));
        var request = { groups: params.groups, zoom: params.zoom, time: params.time };
        ReactDOM.render(React.createElement(CelosMainFetch, { url: "/main", request: request }), document.getElementById('content'));
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

defaultController();
