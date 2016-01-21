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
            React.DOM.table({ className: "workflowTable"},
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
                                return React.DOM.th({ className: "timeHeader", key: i }, tt)
                            })
                    )
                ),
                React.DOM.tbody(null,
                    this.props.data.rows
                        .map(function (product, i) {
                            return React.createElement(ProductRow, {
                                key: i,
                                data: product,
                                store: this.props.store.get(product.workflowName, Immutable.Map()),
                                breadcrumbs: this.props.breadcrumbs.concat(product.workflowName)
                            })}.bind(this)
                        )
                ))
        );
    }
});

var ProductRow = React.createClass({
    displayName: "ProductRow",

    shouldComponentUpdate: function(nextProps, nextState) {
        return nextProps.data !== this.props.data || nextProps.rowState !== this.props.rowState;
    },
    render: function () {
        console.log("render ProductRow", this.props);
        return React.DOM.tr(null,
            React.DOM.th({ className: "workflowName" }, this.props.data.workflowName),
            this.props.data.slots.slice(-slotsNum).map(function (slot, i) {
                return React.createElement(TimeSlot, {
                    key: i,
                    data: slot,
                    status: slot.status,
                    quantity: slot.quantity,
                    ts: slot.ts,
                    workflowName: this.props.data.workflowName,
                    store: this.props.store.get(slot.ts, Immutable.Map()),
                    breadcrumbs: this.props.breadcrumbs.concat(slot.ts)
                });
            }.bind(this))
        )
    }
});

var TimeSlot = React.createClass({
    displayName: "TimeSlot",

    propTypes: {
        store: React.PropTypes.object.isRequired,
        breadcrumbs: React.PropTypes.array.isRequired,
        status: React.PropTypes.string.isRequired,
        workflowName: React.PropTypes.string.isRequired,
        quantity: React.PropTypes.number,
        ts: React.PropTypes.string
    },

    getInitialState: function () {
        return {
            showPopup: false
        }
    },
    handleClick: function (e) {
        //console.log(JSON.stringify(e.nativeEvent), e.altKey, e.altPressed);

        if (e.altKey) {
            console.log("click", e);
            AppDispatcher.handleClickOnSlot({breadcrumbs: this.props.breadcrumbs})
        } else {
            // encapsulated state
            this.setState({showPopup: !this.state.showPopup})
        }
    },
    handleOnMouseLeave: function () {
        this.setState({
            showPopup: false
        })
    },
    getSelectedClass: function () {
        return this.props.store.get("isSelected", false)
    },
    getCellConfig: function () {
        var cell = {};
        var selectedClass = this.getSelectedClass() ? "selected" : "";
        cell.className = ["slot", this.props.status, selectedClass].join(" ");
        cell.onClick = this.handleClick;
        if (this.state.showPopup) {
            cell.onMouseLeave = this.handleOnMouseLeave
        }
        return cell;
    },
    render: function () {
        return (
            React.DOM.td(this.getCellConfig(),
                this.props.quantity
                    ? React.DOM.div(null, this.props.quantity)
                    : null,
                this.state.showPopup
                    ? React.createElement(TriggerStatusFetch, {
                        id: this.props.workflowName,
                        ts: this.props.ts})
                    : null
            )
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
                time: props.ts},
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
            this.drawTrigger([this.state.data]))
            : null
    }
});















