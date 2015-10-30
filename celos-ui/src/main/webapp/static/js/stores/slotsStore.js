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


var _internalSlotsData = undefined;

var _internalLastSelectedSlotPath = undefined;

var _internalNavigationData = {};

var SlotsStore = Object.assign({}, EventEmitter.prototype, {

    getAll: function () {
        return _internalSlotsData;
    },

    getNavigation: function () {
        return _internalNavigationData;
    },

    getSelectedSlots: function () {
        var result = [];
        _internalSlotsData.get("rows").forEach(function (table) {
            table.get("rows").forEach(function (row) {
                row.get("slots").forEach(function (slot, timestamp) {
                    if (slot.get("isSelected", false)) {
                        slot.getIn(["timestamps"]).forEach(function(tmp) {
                            result.push({ workflow: row.get("workflowName"), ts: tmp})
                        })
                    }
                })
            })
        });
        return result;
    }

});

AppDispatcher.register(function (payload) {

    console.log("dispatcherIndex: AppDispatcher.register", payload.action);

    switch (payload.source) {

        case TodoConstants.CLEAR_SELECTION:
            console.log("case TodoConstants.CLEAR_SELECTION");
            var oldVal = payload.breadcrumbs && _internalSlotsData.getIn(payload.breadcrumbs);
            _internalSlotsData = _internalSlotsData.update("rows", function (allGroups) {
                return allGroups.map(function (group) {
                    return group.update("rows", function (workflowList) {
                        return workflowList.map(function (wf) {
                            return wf.update("slots", function (slotList) {
                                return slotList.map(function (slot) {
                                    return slot.set("isSelected", false)
                                })
                            })
                        })
                    })
                })
            });
            if (payload.breadcrumbs) {
                _internalSlotsData = _internalSlotsData.setIn(payload.breadcrumbs, oldVal);
            }
            SlotsStore.emit(CHANGE_EVENT);
            break;

        case TodoConstants.TODO_UPDATE:
            console.log("case TodoConstants.TODO_UPDATE");
            var treePath1 = payload.breadcrumbs.concat("isSelected");
            _internalSlotsData = _internalSlotsData.updateIn(treePath1, function (x) {return !x});
            SlotsStore.emit(CHANGE_EVENT);
            break;

        case TodoConstants.FOCUS_ON_SLOT:
            console.log("case TodoConstants.SIDEBAR_UPDATE");
            var newPath = payload.breadcrumbs;
            var oldPath = _internalLastSelectedSlotPath;
            var newState = _internalSlotsData;
            if (oldPath != undefined) {
                newState = newState.setIn(oldPath.concat("inFocus"), false);
                _internalLastSelectedSlotPath = undefined;
            }
            if (!newPath.equals(oldPath)) {
                var selected = newState.getIn(newPath.concat("isSelected"));
                if (selected) {
                    newState = newState.setIn(newPath.concat("inFocus"), true);
                    _internalLastSelectedSlotPath = newPath;
                } else {
                    newState = newState.setIn(newPath.concat("inFocus"), false);
                    _internalLastSelectedSlotPath = undefined;
                }
            }
            _internalSlotsData = newState;
            SlotsStore.emit(CHANGE_EVENT);
            break;

        case TodoConstants.RECTANGLE_UPDATE:
            console.log("case TodoConstants.RECTANGLE_UPDATE");
            var newPath1 = payload.breadcrumbs;
            var oldPath1 = _internalLastSelectedSlotPath;
            if (oldPath1 == undefined) {
                newState = _internalSlotsData;
                newState = newState.setIn(newPath1.concat("isSelected", true));
                newState = newState.setIn(newPath1.concat("inFocus", true));
                _internalLastSelectedSlotPath = newPath1;
                _internalSlotsData = newState;
                SlotsStore.emit(CHANGE_EVENT);
            }
            var g1 = newPath1.get(1);
            var g2 = oldPath1.get(1);
            if (g1 != g2) {
                console.log("g1 == g2");
                break
            }
            // else
            var wfTop     = Math.min(newPath1.get(3), oldPath1.get(3));
            var wfBottom  = Math.max(newPath1.get(3), oldPath1.get(3));
            var slotLeft  = Math.min(newPath1.get(5), oldPath1.get(5));
            var slotRight = Math.max(newPath1.get(5), oldPath1.get(5));

            newState = _internalSlotsData;
            newState = newState.updateIn(["rows", g1], function (group) {
                return group.update("rows", function (workflowList) {
                    return workflowList.map(function (wf, workflowIdx) {
                        return wf.update("slots", function (slotList) {
                            return slotList.map(function (slot, slotIdx) {
                                if (slotLeft <= slotIdx && slotIdx <= slotRight
                                    && wfTop <= workflowIdx && workflowIdx <= wfBottom
                                    && slot.get("status") != "EMPTY")
                                {
                                    return slot.set("isSelected", true)
                                } else {
                                    return slot
                                }
                            })
                        })
                    })
                })
            });
            _internalSlotsData = newState;
            SlotsStore.emit(CHANGE_EVENT);
            break;

        case TodoConstants.LOAD_SLOTS:
            console.log("case LOAD_SLOTS:");
                ajaxGetJson(
                    /*url=*/ "/group",
                    /*data=*/ {
                        count: slotsNum,
                        group: payload.action.group,
                        zoom: payload.action.zoom,
                        time: payload.action.time
                    },
                    /*success=*/ function (data) {
                        _internalSlotsData = _internalSlotsData.mergeDeepIn(payload.breadcrumbs, Immutable.fromJS(data));
                        SlotsStore.emit(CHANGE_EVENT);
                    }.bind(this)
                );
            break;

        case TodoConstants.LOAD_NAVIGATION:
            console.log("case LOAD_NAVIGATION:");
            var nav = getNavigation(payload.action.zoom, payload.action.time);

            _internalSlotsData = _internalSlotsData.set("navigation", Immutable.fromJS(nav));
            console.log("new state", _internalSlotsData.get("navigation").toJS());
            SlotsStore.emit(CHANGE_EVENT);
            break;

        case TodoConstants.MODAL_BOX:
            console.log("case MODAL_BOX:");
            _internalSlotsData = _internalSlotsData.set("modalBox", Immutable.Map(payload.action));
            SlotsStore.emit(CHANGE_EVENT);
            break;

        // add more cases for other actionTypes, like TODO_DESTROY, etc.
    }

    return true; // No errors. Needed by promise in Dispatcher.
});
