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

var CelosMainFetch = React.createClass({
    displayName: "CelosMainFetch",

    getInitialState: function () {
        return {
            navigation: SlotsStore.getNavigation(),
            data: SlotsStore.getAll()
        };
    },

    _onChange: function() {
        this.setState({
            data: SlotsStore.getAll()
        })
    },

    componentDidMount: function() {
        SlotsStore.on(CHANGE_EVENT, this._onChange)
    },

    componentWillUnmount: function() {
        SlotsStore.removeListener(CHANGE_EVENT, this._onChange)
    },

    componentWillMount: function () {
        var nextProps = this.props;
        AppDispatcher.handleLoadGroupsFromServer({
            url: nextProps.url,
            zoom: nextProps.request.zoom,
            time: nextProps.request.time
        })
    },

    componentWillReceiveProps: function (nextProps) {
        this.loadCommentsFromServer(nextProps);
    },

    render: function () {
        var tmp = this.state.data;
        console.log("CelosMainFetch", tmp.toJS());
        if (tmp.isEmpty()) {
            return React.DOM.div()
        }
        // else
        var activeGroups = [];
        if (this.props.request.groups) {
            var groupFilter = this.props.request.groups;
            tmp.forEach(function (val, groupName) {
                if (groupFilter.indexOf(groupName) >= 0) {
                    activeGroups.push(groupName)
                }
            })
        } else {
            tmp.forEach(function (val, groupName) {
                activeGroups.push(groupName)
            })
        }
        return React.DOM.div(null,
            React.createElement(CelosMain, {
                groups: this.state.data,
                request: this.props.request,
                activeGroups: Immutable.Set(activeGroups),
                navigation: this.state.navigation
            }),
            React.createElement(CelosSidebar, {})
        );
    }
});


var CelosMain = React.createClass({
    displayName: "CelosMain",

    handleContextMenu: function (e) {
        e.preventDefault();
        // show context menu
        ReactDOM.render(React.createElement(ContextMenu, {showElement: true, x: e.pageX, y: e.pageY}),
            document.getElementById('contextMenu'));
        // return false; doesn't work for react events
    },

    render: function () {
        console.log("CelosMain", this.props);
        return React.DOM.div({id: "page-content"},
            React.createElement(ContextMenu, {}),
            React.createElement("h2", null, this.props.navigation.currentTime),
            React.createElement(Navigation, { data: this.props.navigation, request: this.props.request }),
            React.DOM.div({onContextMenu: this.handleContextMenu},
                this.props.groups.map(function (wfInfo, wfGroup) {
                    var isGroupActive = this.props.activeGroups.contains(wfGroup);
                    if (isGroupActive) {
                        return React.DOM.div({ key: wfGroup },
                            React.createElement(WorkflowsGroupFetch, {
                                name: wfGroup,
                                active: isGroupActive,
                                request: this.props.request,
                                rows: wfInfo,
                                breadcrumbs: [].concat(wfGroup)
                            }),
                            React.DOM.br())
                    } else {
                        var req = this.props.request;
                        var newUrl = makeCelosHref(req.zoom, req.time, req.groups.concat(wfGroup));
                        return React.DOM.div({ key: wfGroup, className: "groupName"},
                            React.DOM.a({ href: newUrl }, wfGroup)
                        )
                    }
                }.bind(this)).valueSeq().toJS()
            )
        );
    }
});

var ContextMenu = React.createClass({
    displayName: "ContextMenu",

    proceedClick1: function () {
        ajaxPostJSON("/multiplex-rpc",
            {
                action: "rerun",
                slots: SlotsStore.getSelectedSlots()
            }, function (res) { console.log(res)}
        )
    },

    proceedClick2: function () {
        ajaxPostJSON("/multiplex-rpc",
            {
                action: "kill",
                slots: SlotsStore.getSelectedSlots()
            }, function (res) { console.log(res)}
        )
    },

    render: function () {
        if (!this.props.showElement) {
            return null
        }
        // else
        return (
            React.DOM.ul({className: "dropdown-menu",
                          style: {
                              display: "block",
                              position: "absolute",
                              top: this.props.y,
                              left: this.props.x
                          }},
                React.DOM.li({onClick: this.proceedClick1},
                    React.DOM.a({href: window.location.hash, tabIndex: -1},
                        "Kill workflows")),
                React.DOM.li({onClick: this.proceedClick2},
                    React.DOM.a({href: window.location.hash, tabIndex: -1},
                        "Rerun workflows"))
            )
        )
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
            React.DOM.a({ href: makeCelosHref(this.props.data.zoomOut, this.props.request.time, this.props.request.groups) }, "- Zoom OUT"),
            React.DOM.strong(null, " / ", this.props.request.zoom || 60, " minutes / "),
            React.DOM.a({ href: makeCelosHref(this.props.data.zoomIn, this.props.request.time, this.props.request.groups) }, "Zoom IN +"),
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
