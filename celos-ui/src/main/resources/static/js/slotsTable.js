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

var WorkflowsGroup = React.createClass({
    displayName: "WorkflowsGroup",

    getInitialState: function () {
        return {
            selectedSlots: Immutable.Map()
        }
    },

    clickOnSlot: function (wfName, slotTimestamp) {
        if (slotTimestamp !== null) {
            var newSlots = this.state.selectedSlots.updateIn([wfName, slotTimestamp, "isSelected"], false, function (x) {
                return !x;
            });
            this.setState({
                selectedSlots: newSlots
            })
        }
    },

    handleOnContextMenu: function (e) {
        console.log(e);
        // show context menu
        ReactDOM.render(React.createElement(ContextMenu, {showElement: true, x: e.pageX, y: e.pageY}),
            document.getElementById('contextMenu'));

        return false;
    },


    render: function () {
        console.log("render WorkflowsGroup", this.props);
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
        return (
            React.DOM.table({ className: "workflowTable", onContextMenu: this.handleOnContextMenu},
                React.DOM.thead(null,
                    React.DOM.tr(null,
                        React.DOM.th({ className: "groupName" },
                            React.DOM.a({ href: newUrl }, this.props.data.name)),
                        this.props.data.days
                            .slice(-slotsNum)
                            .map(function (tt, i) {
                                return React.DOM.th({ className: "timeHeader", key: i },
                                    React.DOM.strong(null, tt));
                            })
                    ),
                    React.DOM.tr(null,
                        React.DOM.th({ className: "groupName" }),
                        this.props.data.times
                            .slice(-slotsNum)
                            .map(function (tt, i) {
                                return React.DOM.th({ className: "timeHeader", key: i }, tt);
                            })
                    )),
                React.DOM.tbody(null,
                    this.props.data.rows
                        .map(function (product, i) {
                            return React.createElement(ProductRow, {
                                key: i,
                                data: product,
                                rowState: this.state.selectedSlots.get(product.workflowName, Immutable.Map()),
                                tableHandler: this
                            });
                        }.bind(this))
                ))
        );
    }
});

var ProductRow = React.createClass({
    displayName: "ProductRow",

    shouldComponentUpdate: function(nextProps, nextState) {
        return nextProps.data !== this.props.data || nextProps.rowState !== this.props.rowState;
    },
    render: function render() {
        console.log("render ProductRow", this.props);
        var defaultSlotState = Immutable.Map({isSelected: false});
        return React.DOM.tr(null,
            React.DOM.th({ className: "workflowName" }, this.props.data.workflowName),
            this.props.data.slots.slice(-slotsNum).map(function (slot, i) {
                return React.createElement(TimeSlot, {
                    key: i,
                    data: slot,
                    workflowName: this.props.data.workflowName,
                    slotState: this.props.rowState.get(slot.ts, defaultSlotState),
                    tableHandler: this.props.tableHandler
                });
            }.bind(this))
        )
    }
});

var TimeSlot = React.createClass({
    displayName: "TimeSlot",

    getInitialState: function () {
        return {
            showPopup: false
        }
    },
    handleClick: function () {
        if (KEYBOARD.altPressed) {
            this.props.tableHandler.clickOnSlot(this.props.workflowName, this.props.data.ts)
        } else {
            this.setState({
                showPopup: !this.state.showPopup
            })
        }
    },
    handleOnMouseLeave: function () {
        this.setState({
            showPopup: false
        })
    },
    makePopupElementOrNull: function() {
        if (!this.state.showPopup) {
            return null
        } else {
            return React.DOM.div({className: "cell-hover"},
                React.DOM.a({href: this.props.data.url}, "click me!"),
                "Slot info")
        }
    },
    getCellConfig: function () {
        var cell = {};
        var selectedClass = this.props.slotState.get("isSelected") ? "selected" : "";
        cell.className = ["slot", this.props.data.status, selectedClass].join(" ");
        cell.onClick = this.handleClick;
        if (this.state.showPopup) {
            cell.onMouseLeave = this.handleOnMouseLeave
        }
        return cell;
    },
    render: function () {
        return (
            React.DOM.td(this.getCellConfig(),
                this.props.data.quantity
                    ? React.DOM.div(null, this.props.data.quantity)
                    : null,
                this.makePopupElementOrNull()
            )
        )
    }
});
