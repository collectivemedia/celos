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
        if (this.props.rows) {
            console.log("WorkflowsGroupFetch", this.props.rows.toJS());
            return React.createElement(WorkflowsGroup, {
                request: this.props.request,
                groupName: this.props.name,
                store: this.props.rows,
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
        store: React.PropTypes.instanceOf(Immutable.Map).isRequired,
        breadcrumbs: React.PropTypes.arrayOf(React.PropTypes.string).isRequired
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

    render: function () {
        console.log("render WorkflowsGroup", this.props.store.get("rows").toJS());
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
        var newUrl = makeCelosHref(req.zoom, req.time, newGroups);
        return (
            React.DOM.table({ className: "workflowTable"},
                React.DOM.thead(null,
                    React.DOM.tr(null,
                        React.DOM.th({ className: "groupName" },
                            React.DOM.a({ href: newUrl }, this.props.groupName)),
                        this.props.store.get("days")
                            .slice(-slotsNum)
                            .map(function (tt, i) {
                                return React.DOM.th({ className: "timeHeader", key: i },
                                    React.DOM.strong(null, tt));
                            })
                    ),
                    React.DOM.tr(null,
                        React.DOM.th({ className: "groupName" }),
                        this.props.store.get("times")
                            .slice(-slotsNum)
                            .map(function (tt, i) {
                                return React.DOM.th({ className: "timeHeader", key: i }, tt)
                            })
                    )
                ),
                React.DOM.tbody(null,
                    this.props.store.get("rows")
                        .map(function (product, i) {
                            return React.createElement(ProductRow, {
                                key: i,
                                store: product,
                                //store: this.props.store.get(product.workflowName, Immutable.Map()),
                                breadcrumbs: this.props.breadcrumbs.concat(product.workflowName)
                            })}.bind(this))
                )
            )
        );
    }
});

var ProductRow = React.createClass({
    displayName: "ProductRow",

    shouldComponentUpdate: function(nextProps, nextState) {
        return nextProps.data !== this.props.data || nextProps.rowState !== this.props.rowState;
    },
    render: function () {
        console.log("render ProductRow", this.props.store.toJS());
        return React.DOM.tr(null,
            React.DOM.th({ className: "workflowName" }, this.props.store.get("workflowName")),
            this.props.store.get("slots")
                .slice(-slotsNum)
                .map(function (slot1, i) {
                    var slot = slot1.toJS();
                    return React.createElement(TimeSlot, {
                        key: i,
                        data: slot,
                        status: slot.status,
                        quantity: slot.quantity,
                        timestamps: slot.timestamps,
                        workflowName: this.props.store.get("workflowName"),
                        store: this.props.store.get(slot.timestamps[0], Immutable.Map()),
                        breadcrumbs: this.props.breadcrumbs.concat(slot.timestamps[0])
                    });
                }.bind(this))
        )
    }
});

var TimeSlot = React.createClass({
    displayName: "TimeSlot",

    propTypes: {
        store: React.PropTypes.instanceOf(Immutable.Map),
        breadcrumbs: React.PropTypes.array.isRequired,
        status: React.PropTypes.string.isRequired,
        workflowName: React.PropTypes.string.isRequired,
        quantity: React.PropTypes.number,
        timestamps: React.PropTypes.array
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

    getSelectedClass: function () {
        return this.props.store.get("isSelected", false)
    },

    getCellConfig: function () {
        var cell = {};
        var selectedClass = this.getSelectedClass() ? "selected" : "";
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














