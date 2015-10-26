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


var CelosSidebar = React.createClass({
    displayName: "CelosSidebar",

    getInitialState: function () {
        return {
            store: SidebarStore.getAll(),
            dropdownActivated: false
        }
    },

    _onChange: function() {
        console.log("_onChange", SidebarStore.getAll().toJS());
        this.setState({
            store: SidebarStore.getAll()
        })
    },

    componentDidMount: function() {
        SidebarStore.on(CHANGE_EVENT, this._onChange)
    },

    componentWillUnmount: function() {
        SidebarStore.removeListener(CHANGE_EVENT, this._onChange)
    },

    menuInternals: function () {
        return React.createElement("div", { className: "dropdown-menu", "aria-labelledby": "dropdownMenu1" },
            React.createElement(
                "button",
                { className: "dropdown-item", type: "button" },
                "parqutifly-retarget@2015-10-05T12:21"
            ),
            React.createElement(
                "button",
                { className: "dropdown-item", type: "button" },
                "parqutifly-retarget@2015-10-05T12:22"
            ),
            React.createElement(
                "button",
                { className: "dropdown-item", type: "button" },
                "parqutifly-retarget@2015-10-05T12:23"
            )
        )
    },

    makeDropMenu: function () {
        return React.DOM.div({ className: "dropdown open" },
            React.DOM.button({
                    className: "btn btn-secondary dropdown-toggle",
                    type: "button",
                    id: "dropdownMenu1",
                    "data-toggle": "dropdown",
                    "aria-haspopup": "true",
                    "aria-expanded": this.state.dropdownActivated
                },
                "parqutifly-retarget@2015-10-05T12:21"
            ),
            this.state.dropdownActivated
                ? this.menuInternals()
                : null
            )
    },

    render: function () {
        console.log("!!!!", this.state.store.toJS());
        return React.DOM.div({id: "sidebar-wrapper"},
            React.createElement(
                "h4",
                { align: "center" },
                "Selection (XX slots)"
            ),
            React.DOM.ul({ className: "my-row" },
                React.DOM.li({ className: "my-column" },
                    this.makeDropMenu()
                ),
                React.DOM.li({ className: "my-column" },
                    React.createElement(
                        "button",
                        { type: "button", className: "btn btn-warning " },
                        "Rerun"
                    )
                ),
                React.DOM.li({ className: "my-column" },
                    React.createElement(
                        "button",
                        { type: "button", className: "btn btn-danger" },
                        "Kill"
                    )
                )
            ),
            React.DOM.ul({ className: "nav nav-tabs" },
                React.DOM.li({ className: "nav-item" },
                    React.createElement(
                        "a",
                        { href: "#", className: "nav-link" },
                        "Overview"
                    )
                ),
                React.DOM.li({ className: "nav-item" },
                    React.createElement(
                        "a",
                        { href: "#", className: "nav-link active" },
                        "Trigger"
                    )
                ),
                React.DOM.li({ className: "nav-item disabled" },
                    React.createElement(
                        "a",
                        { href: "#", className: "nav-link" },
                        "Logs"
                    )
                ),
                React.DOM.li({ className: "nav-item disabled" },
                    React.createElement(
                        "a",
                        { href: "#", className: "nav-link" },
                        "Javasript"
                    )
                )
            ),
            this.state.store.getIn(["selected", "workflowName"])
                ? React.createElement(TriggerStatusFetch, {
                                    id: this.state.store.getIn(["selected", "workflowName"]),
                                    timestamps: this.state.store.getIn(["selected", "ts"]),
                                    quantity: 13})
                : null

            )
        }

});


var TriggerStatusFetch = React.createClass({
    displayName: "TriggerStatusFetch",

    loadCommentsFromServer: function (props) {
        console.log("TriggerStatusFetch fromServer:", props);
        ajaxGetJson(
            /*url=*/ "/trigger-status",
            /*data=*/ {
                id: props.id,
                time: props.timestamps.join(",")},
            /*success=*/ function (data) {
                console.log("set State:", data);
                this.setState({ data: data })
            }.bind(this)
        )
    },
    componentWillMount: function () {
        this.loadCommentsFromServer(this.props)
    },
    componentWillReceiveProps: function (nextProps) {
        this.loadCommentsFromServer(nextProps)
    },

    drawTrigger: function(listOfStatuses) {
        console.log(listOfStatuses);
        return React.DOM.ul({className: "my-non-styled-list"},
            listOfStatuses.map(function (curr) {
                var successClass = curr.ready ? "label-success" : "label-warning";
                var successLabel = curr.ready ? "READY" : "WAIT";
                var header = React.DOM.li(null,
                    React.DOM.span({className: ["label", successClass].join(" ")}, successLabel),
                    " ",
                    curr.type,
                    ": ",
                    curr.description
                );
                var children = React.DOM.li(null,
                    this.drawTrigger(curr.subStatuses)
                );
                return [header, children]
            }.bind(this)))
    },

    render: function () {
        console.log("TriggerStatus render", this.props, this.state);
        return this.state
            ? React.DOM.div({className: "cell-hover"},
            this.drawTrigger(this.state.data))
            : null
    }
});


