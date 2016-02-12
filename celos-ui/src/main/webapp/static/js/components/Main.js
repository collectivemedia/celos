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

var CelosMainFetch = React.createClass({
    displayName: "CelosMainFetch",

    getInitialState: function () {
        return {
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
        AppDispatcher.handleLoadNavigation({
            url: nextProps.url,
            zoom: nextProps.request.zoom,
            time: nextProps.request.time
        })
    },

    componentWillReceiveProps: function (nextProps) {
        AppDispatcher.clearSelection();
        AppDispatcher.handleLoadNavigation({
            url: nextProps.url,
            zoom: nextProps.request.zoom,
            time: nextProps.request.time
        })
    },

    render: function () {
        var tmp = this.state.data.get("rows");
        if (tmp === undefined) {
            return React.DOM.div()
        }
        // else
        var activeGroups = [];
        var groupFilter = this.props.request.groups;
        if (groupFilter) {
            tmp.forEach(function (group) {
                if (groupFilter.indexOf(group.get("name")) >= 0) {
                    activeGroups.push(group.get("name"))
                }
            })
        } else {
            tmp.forEach(function (group) {
                activeGroups.push(group.get("name"))
            })
        }
        return React.DOM.div(null,
            React.createElement(CelosMain, {
                groups: tmp,
                modalBox: this.state.data.get("modalBox", Immutable.Map()),
                request: this.props.request,
                activeGroups: Immutable.Set(activeGroups),
                navigation: this.state.data.get("navigation")
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
        return React.DOM.div({id: "page-content"},
            React.createElement(ContextMenu, {}),
            React.createElement(ModalBox, {store: this.props.modalBox}),
            React.DOM.h2(null, this.props.navigation.currentTime),
            React.createElement(Navigation, { data: this.props.navigation, request: this.props.request }),
            React.DOM.p({style: {backgroundColor: "#eee"}}, "to select slots use Alt + Click"),
            React.DOM.div({onContextMenu: this.handleContextMenu},
                this.props.groups.map(function (wfInfo, i) {
                    var wfGroup = wfInfo.get("name");
                    var isGroupActive = this.props.activeGroups.contains(wfGroup);
                    if (isGroupActive) {
                        return React.DOM.div({ key: i },
                            React.createElement(WorkflowsGroupFetch, {
                                name: wfGroup,
                                active: isGroupActive,
                                request: this.props.request,
                                store: wfInfo,
                                breadcrumbs: Immutable.Seq(["rows", i])
                            }),
                            React.DOM.br())
                    } else {
                        var req = this.props.request;
                        var newUrl = makeCelosHref(req.zoom, req.time, req.groups.concat(wfGroup));
                        return React.DOM.div({ key: i, className: "groupName"},
                            React.DOM.a({ href: newUrl }, wfGroup)
                        )
                    }
                }.bind(this)).valueSeq().toJS()
            )
        );
    }
});
