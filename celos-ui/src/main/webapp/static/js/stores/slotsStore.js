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


var _internalSlotsData = Immutable.Map();

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
                        result.push({ workflow: row.get("workflowName"), ts: slot.getIn(["timestamps", 0])})
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

        case TodoConstants.TODO_UPDATE:
            console.log("case TodoConstants.TODO_UPDATE");
            var treePath1 = payload.breadcrumbs.concat("isSelected");
            _internalSlotsData = _internalSlotsData.updateIn(treePath1, function (x) {return !x});
            SlotsStore.emit(CHANGE_EVENT);
            break;

        case TodoConstants.SIDEBAR_UPDATE:
            console.log("case TodoConstants.SIDEBAR_UPDATE");
            var newPath = payload.breadcrumbs.concat("temporarySelected");
            var oldPath = _internalLastSelectedSlotPath;
            var newState = _internalSlotsData;

            console.log("DEBUG:", oldPath, newPath, oldPath == newPath);

            if (oldPath != undefined) {
                newState = newState.setIn(oldPath, false);
                _internalLastSelectedSlotPath = undefined;
            }
            if (!newPath.equals(oldPath)) {
                newState = newState.setIn(newPath, true);
                _internalLastSelectedSlotPath = newPath;
            }
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
                        console.log("?????", _internalSlotsData.getIn(payload.breadcrumbs).toJS());
                        SlotsStore.emit(CHANGE_EVENT);
                    }.bind(this)
                );
            break;

        case TodoConstants.LOAD_GROUPS:
            console.log("case LOAD_GROUPS:");
            ajaxGetJson(
                /*url=*/ payload.action.url,
                /*data=*/ {
                    zoom: payload.action.zoom,
                    time: payload.action.time
                },
                /*success=*/ (function (data) {
                    // deep merge works fine with empty lists
                    _internalSlotsData = _internalSlotsData.mergeDeep(Immutable.fromJS(data));
                    //console.log("new state", _internalSlotsData.toJS());
                    SlotsStore.emit(CHANGE_EVENT);
                }).bind(this)
            );
            break;

        // add more cases for other actionTypes, like TODO_DESTROY, etc.
    }

    return true; // No errors. Needed by promise in Dispatcher.
});
