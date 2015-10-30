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

var SIDEBAR_TABS = {
    OVERVIEW: "Overview",
    TRIGGER: "Trigger",
    LOGS: "Logs",
    JAVASCRIPT: "Javascript"
};


var CelosSidebar = React.createClass({
    displayName: "CelosSidebar",

    getInitialState: function () {
        return {
            store: SidebarStore.getAll(),
            dropdownActivated: false,
            selectedTab: SIDEBAR_TABS.TRIGGER,
            selectedSlots: []
        }
    },

    _onChange: function() {
        console.log("_onChange", SidebarStore.getAll().toJS());
        this.setState({
            store: SidebarStore.getAll(),
            selectedSlots: SlotsStore.getSelectedSlots()
        })
    },

    componentDidMount: function() {
        SidebarStore.on(CHANGE_EVENT, this._onChange);
        SlotsStore.on(CHANGE_EVENT, this._onChange)
    },

    componentWillUnmount: function() {
        SidebarStore.removeListener(CHANGE_EVENT, this._onChange);
        SlotsStore.removeListener(CHANGE_EVENT, this._onChange)
    },

    menuInternals: function () {
        console.log("this.state.selectedSlots");
        console.log(this.state.selectedSlots);
        return React.DOM.div({ className: "dropdown-menu overflow-scroll", "aria-labelledby": "dropdownMenu1" },
            (this.state.selectedSlots.length == 0)
                ? React.DOM.div({ className: "dropdown-item"}, "empty list...")
                : this.state.selectedSlots.map(function (elem, i) {
                    return React.createElement("button",
                        { className: "dropdown-item", type: "button", key: i},
                        "" + elem.workflow + "@" + elem.ts
                    )
                })
        )
    },

    handleClickDropdown: function () {
        this.setState({dropdownActivated: !this.state.dropdownActivated})
    },

    makeDropMenu: function () {
        return React.DOM.div({ className: "dropdown open" },
            React.DOM.button({
                    onClick: this.handleClickDropdown,
                    className: "btn btn-secondary dropdown-toggle",
                    type: "button",
                    id: "dropdownMenu1",
                    "data-toggle": "dropdown",
                    "aria-haspopup": "true",
                    "aria-expanded": this.state.dropdownActivated
                },
                "SELECT SLOT"
            ),
            this.state.dropdownActivated
                ? this.menuInternals()
                : null
            )
    },

    makeHandleSelectTab: function (tabLabel) {
        return function () {
            this.setState({
                selectedTab: tabLabel
            })
        }.bind(this)
    },

    makeOneTab: function (tabLabel) {
        return React.DOM.li({ className: "nav-item", onClick: this.makeHandleSelectTab(tabLabel)},
            React.DOM.a({
                    href: window.location.hash,
                    className: "nav-link" + (this.state.selectedTab == tabLabel ? " active" : "")
                },
                tabLabel
            )
        )
    },

    makeTabContent: function () {
        switch (this.state.selectedTab) {
            case SIDEBAR_TABS.OVERVIEW:
                return "in development...";
            case SIDEBAR_TABS.TRIGGER:
                if (this.state.store.getIn(["selected", "workflowName"])) {
                    return React.createElement(TriggerStatusFetch, {
                        id: this.state.store.getIn(["selected", "workflowName"]),
                        timestamps: this.state.store.getIn(["selected", "ts"]),
                        quantity: 13})
                } else {
                    return "slot is not selected"
                }
            case SIDEBAR_TABS.LOGS:
                return "in development...";
            case SIDEBAR_TABS.JAVASCRIPT:
                return "in development...";
        }

    },

    makeNavTabs: function () {
        return React.DOM.div(null,
            React.DOM.ul({ className: "nav nav-tabs" },
                this.makeOneTab(SIDEBAR_TABS.OVERVIEW),
                this.makeOneTab(SIDEBAR_TABS.TRIGGER),
                this.makeOneTab(SIDEBAR_TABS.LOGS),
                this.makeOneTab(SIDEBAR_TABS.JAVASCRIPT)
            ),
            this.makeTabContent()

        )
    },

    render: function () {
        console.log("CelosSidebar", this.state.store.toJS());
        var wfName = this.state.store.getIn(["selected", "workflowName"]);
        return React.DOM.div({id: "sidebar-wrapper"},
            React.DOM.h4({style: {textAlign: "center"}},
                "Selection (" + this.state.selectedSlots.length + " slots)"
            ),
            React.DOM.h6({style: {textAlign: "left"}},
                (wfName)
                    ? wfName + "@" + this.state.store.getIn(["selected", "ts", 0])
                    : "not selected"
            ),
            React.DOM.ul({ className: "my-row" },
                React.DOM.li({ className: "my-column" },
                    this.makeDropMenu()
                ),
                React.DOM.li({ className: "my-column" },
                    React.DOM.button({ type: "button", className: "btn btn-warning " },
                        "Rerun"
                    )
                ),
                React.DOM.li({ className: "my-column" },
                    React.DOM.button({ type: "button", className: "btn btn-danger" },
                        "Kill"
                    )
                )
            ),
            this.makeNavTabs()
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
        //console.log("TriggerStatus render", this.props, this.state);
        return this.state
            ? React.DOM.div({className: "cell-hover"},
            this.drawTrigger(this.state.data))
            : null
    }
});

