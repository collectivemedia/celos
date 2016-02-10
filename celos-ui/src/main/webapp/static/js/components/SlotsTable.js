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

    componentWillReceiveProps: function (nextProps) {
        if (nextProps.request.zoom != this.props.request.zoom
            ||  nextProps.request.time != this.props.request.time
        ) {
            AppDispatcher.handleLoadSlotsFromServer({
                group: nextProps.name,
                zoom: nextProps.request.zoom,
                time: nextProps.request.time,
                breadcrumbs: nextProps.breadcrumbs
            })
        }
    },

    render: function render() {
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
        rows: React.PropTypes.instanceOf(Immutable.List).isRequired,
        times: React.PropTypes.instanceOf(Immutable.List).isRequired,
        days: React.PropTypes.instanceOf(Immutable.List).isRequired,
        breadcrumbs: React.PropTypes.instanceOf(Immutable.Seq).isRequired
    },

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
        var newUrl = this.makeNewUrl();
        return (
            React.DOM.table({ className: "workflowTable"},
                React.DOM.thead(null,
                    React.DOM.tr(null,
                        React.DOM.th({ className: "groupName" },
                            React.DOM.a({ href: newUrl }, this.props.groupName)),
                        this.props.days
                            .take(slotsNum)
                            .map(function (tt, i) {
                                return React.DOM.th({ className: "timeHeader", key: i },
                                    React.DOM.strong(null, tt))
                            })
                    ),
                    React.DOM.tr(null,
                        React.DOM.th({ className: "groupName" }),
                        this.props.times
                            .take(slotsNum)
                            .map(function (tt, i) {
                                return React.DOM.th({ className: "timeHeader", key: i }, tt)
                            })
                    )
                ),
                // this is table rows, don't reverse them
                React.DOM.tbody(null,
                    this.props.rows
                        .map(function (product, i) {
                            return React.createElement(ProductRow, {
                                key: i,
                                slots: product.get("rows"),
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
        slots: React.PropTypes.instanceOf(Immutable.List).isRequired,
        workflowName: React.PropTypes.string.isRequired,
        breadcrumbs: React.PropTypes.instanceOf(Immutable.Seq).isRequired
    },

    shouldComponentUpdate: function(nextProps) {
//    FIXME improve speed here, change equals to ===
        return !(
                nextProps.slots.equals(this.props.slots)
             && nextProps.workflowName == this.props.workflowName)
    },

    render: function () {
        return React.DOM.tr(null,
            React.DOM.th({ className: "workflowName" }, this.props.workflowName),
            this.props.slots
                .take(slotsNum)
                //.slice(-slotsNum)
                .map(function (slot1, i1) {
                    //var i = i1 + shift;
                    return React.createElement(TimeSlot, {
                        key: i1,
                        store: slot1,
                        workflowName: this.props.workflowName,
                        // slots were reversed, so it should have different indexes in the model and in owr view
                        breadcrumbs: this.props.breadcrumbs.concat("rows", i1)
                    })
                }.bind(this))
        )
    }
});

var TimeSlot = React.createClass({
    displayName: "TimeSlot",

    propTypes: {
        store: React.PropTypes.instanceOf(Immutable.Map).isRequired,
        workflowName: React.PropTypes.string.isRequired,
        breadcrumbs: React.PropTypes.instanceOf(Immutable.Seq).isRequired
    },

    shouldComponentUpdate: function(nextProps) {
        return nextProps.store !== this.props.store
            || nextProps.workflowName != this.props.workflowName
    },

    getInitialState: function () {
        return {
            showPopup: false
        }
    },

    handleClick: function (e) {
        // empty status do nothing
        if (this.props.store.get("status") == "EMPTY") {
            return
        }
        var focusArgs = SlotRecord({
            ts: this.props.store.get("timestamps"),
            workflowName: this.props.workflowName,
            breadcrumbs: this.props.breadcrumbs.concat("timestamps", 0)
        });
        if (e.altKey) {
            AppDispatcher.markSlotAsSelected({
                breadcrumbs: this.props.breadcrumbs
            });
            AppDispatcher.focusOnSlot(focusArgs);
        } else if (e.shiftKey) {
            AppDispatcher.rectangleSlotSelection({
                breadcrumbs: this.props.breadcrumbs
            });
            AppDispatcher.focusOnSlot(focusArgs);
        } else {
            AppDispatcher.clearSelection({
                breadcrumbs: this.props.breadcrumbs
            });
            AppDispatcher.markSlotAsSelected({
                breadcrumbs: this.props.breadcrumbs
            });
            AppDispatcher.focusOnSlot(focusArgs);
        }
    },

    getCellConfig: function (slotInfo) {
        var selectedClass = slotInfo.isSelected ? "selected" : "";
        return {
            className: ["slot", slotInfo.status, selectedClass].join(" "),
            onClick: this.handleClick,
            style: (slotInfo.inFocus ? {border: "dashed 1px"} : undefined)
        }
    },

    render: function () {
        var slotInfo = this.props.store.toJS();
        var quantity = slotInfo.quantity;
        return (
            React.DOM.td(this.getCellConfig(slotInfo),
                quantity > 1
                    ? React.DOM.div(null, quantity)
                    : null,
                this.state.showPopup
                    ? "popup moved to sidebar"
                    : null
            )
        )
    }
});














