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


var WorkflowsGroupFetch = React.createClass({
    displayName: "WorkflowsGroupFetch",

    componentWillMount: function () {
        var nextProps = this.props;
        AppDispatcher.handleLoadSlotsFromServer({
            group: nextProps.name,
            zoom: nextProps.request.zoom,
            time: nextProps.request.time,
            breadcrumbs: nextProps.breadcrumbs
        })
    },

    //componentWillReceiveProps: function (nextProps) {
        //if (nextProps.rows == undefined) {
        //    AppDispatcher.handleLoadSlotsFromServer({
        //        group: nextProps.name,
        //        zoom: nextProps.request.zoom,
        //        time: nextProps.request.time,
        //        breadcrumbs: nextProps.breadcrumbs
        //    })
        //}
    //},

    render: function render() {
        console.log("WorkflowsGroupFetch", this.props.store.toJS());
        if (!this.props.store.get("rows").isEmpty()) {
            return React.createElement(WorkflowsGroup, {
                request: this.props.request,
                groupName: this.props.name,
                rows: this.props.store.get("rows"),
                times: this.props.store.get("times"),
                days: this.props.store.get("days"),
                breadcrumbs: this.props.breadcrumbs
            })
        } else {
            return null
        }
    }
});


var WorkflowsGroup = React.createClass({
    displayName: "WorkflowsGroup",

    propTypes: {
        rows: React.PropTypes.instanceOf(Immutable.List),
        times: React.PropTypes.instanceOf(Immutable.List),
        days: React.PropTypes.instanceOf(Immutable.List),
        breadcrumbs: React.PropTypes.array.isRequired
    },

    //clickOnSlot: function (wfName, slotTimestamp) {
    //    if (slotTimestamp !== null) {
    //        var newSlots = this.state.selectedSlots.updateIn([wfName, slotTimestamp, "isSelected"], false, function (x) {
    //            return !x;
    //        });
    //        this.setState({
    //            selectedSlots: newSlots
    //        })
    //    }
    //},

    makeNewUrl: function () {
        var req = this.props.request;
        var groupName = this.props.groupName;
        var newGroups;
        if (req.groups && req.groups != []) {
            newGroups = req.groups.filter(function (x) {
                return x != groupName;
            });
        } else {
            newGroups = [groupName];
        }
        return makeCelosHref(req.zoom, req.time, newGroups);
    },

    render: function () {
        console.log("WorkflowsGroup", this.props.rows.toJS());
        var newUrl = this.makeNewUrl();
        return (
            React.DOM.table({ className: "workflowTable"},
                React.DOM.thead(null,
                    React.DOM.tr(null,
                        React.DOM.th({ className: "groupName" },
                            React.DOM.a({ href: newUrl }, this.props.groupName)),
                        this.props.days
                            .slice(-slotsNum)
                            .map(function (tt, i) {
                                return React.DOM.th({ className: "timeHeader", key: i },
                                    React.DOM.strong(null, tt))
                            })
                    ),
                    React.DOM.tr(null,
                        React.DOM.th({ className: "groupName" }),
                        this.props.times
                            .slice(-slotsNum)
                            .map(function (tt, i) {
                                return React.DOM.th({ className: "timeHeader", key: i }, tt)
                            })
                    )
                ),
                React.DOM.tbody(null,
                    this.props.rows
                        .map(function (product, i) {
                            return React.createElement(ProductRow, {
                                key: i,
                                slots: product.get("slots"),
                                workflowName: product.get("workflowName"),
                                breadcrumbs: this.props.breadcrumbs.concat("rows", i)
                            })}.bind(this))
                )
            )
        )
    }
});

var ProductRow = React.createClass({
    displayName: "ProductRow",

    propTypes: {
        slots: React.PropTypes.instanceOf(Immutable.List),
        workflowName: React.PropTypes.string.isRequired,
        timestamps: React.PropTypes.array,
        breadcrumbs: React.PropTypes.array.isRequired
    },

    shouldComponentUpdate: function(nextProps) {
        return nextProps.slots !== this.props.slots
            || nextProps.timestamps !== this.props.timestamps
            || nextProps.workflowName !== this.props.workflowName;
    },

    render: function () {
        console.log("render ProductRow", this.props.slots.toJS());
        // shift needs to calculate correct breadcrumbs
        var shift = (this.props.slots.count() > slotsNum)
            ? this.props.slots.count() - slotsNum
            : 0;

        console.log("render ProductRowrender ProductRowrender ProductRow", shift);
        return React.DOM.tr(null,
            React.DOM.th({ className: "workflowName" }, this.props.workflowName),
            this.props.slots
                .slice(-slotsNum)
                .map(function (slot1, i1) {
                    var i = i1 + shift;
                    var slot = slot1.toJS();
                    return React.createElement(TimeSlot, {
                        key: i,
                        status: slot.status,
                        quantity: slot.quantity,
                        timestamps: slot.timestamps,
                        workflowName: this.props.workflowName,
                        isSelected: slot.isSelected || false,
                        breadcrumbs: this.props.breadcrumbs.concat("slots", i)
                    })
                }.bind(this))
        )
    }
});

var TimeSlot = React.createClass({
    displayName: "TimeSlot",

    propTypes: {
        isSelected: React.PropTypes.bool.isRequired,
        status: React.PropTypes.string.isRequired,
        workflowName: React.PropTypes.string.isRequired,
        quantity: React.PropTypes.number,
        timestamps: React.PropTypes.array,
        breadcrumbs: React.PropTypes.array.isRequired
    },

    shouldComponentUpdate: function(nextProps) {
        return nextProps.isSelected !== this.props.isSelected
            || nextProps.status !== this.props.status
            || nextProps.quantity !== this.props.quantity
            || nextProps.timestamps !== this.props.timestamps
            || nextProps.workflowName !== this.props.workflowName;
    },

    getInitialState: function () {
        return {
            showPopup: false
        }
    },

    handleClick: function (e) {
        // empty status do nothing
        if (this.props.status == "EMPTY") {
            return
        }
        console.log("click", e);
        if (e.altKey) {
            AppDispatcher.handleSelectSlot({
                breadcrumbs: this.props.breadcrumbs
            })
        } else {
            AppDispatcher.handleClickOnSlot({
                ts: this.props.timestamps,
                workflowName: this.props.workflowName
            })
        }
    },
    //handleOnMouseLeave: function () {
    //    this.setState({
    //        showPopup: false
    //    })
    //},

    getCellConfig: function () {
        var cell = {};
        var selectedClass = this.props.isSelected ? "selected" : "";
        cell.className = ["slot", this.props.status, selectedClass].join(" ");
        cell.onClick = this.handleClick;
        //if (this.state.showPopup) {
        //    cell.onMouseLeave = this.handleOnMouseLeave
        //}
        return cell;
    },

    render: function () {
        return (
            React.DOM.td(this.getCellConfig(),
                this.props.quantity > 1
                    ? React.DOM.div(null, this.props.quantity)
                    : null,
                this.state.showPopup
                    ? "popup moved to sidebar"
                    : null
            )
        )
    }
});














