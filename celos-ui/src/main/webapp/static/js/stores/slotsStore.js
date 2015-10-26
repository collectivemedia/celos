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
        _internalSlotsData.forEach(function (table) {
            table.forEach(function (row, wfName) {
                row.forEach(function (slot, timestamp) {
                    if (slot.get("isSelected", false)) {
                        result.push({ workflow: wfName, ts: timestamp })
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
            console.log(_internalSlotsData.toJS());
            var treePath = payload.breadcrumbs.concat("isSelected");
            _internalSlotsData = _internalSlotsData.updateIn(treePath, function (x) {return !x});
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
                        _internalSlotsData = _internalSlotsData.set(payload.action.group, Immutable.fromJS(data));
                        SlotsStore.emit(CHANGE_EVENT)
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
                    console.log("success", data);
                    var tmp = data.rows.map(function (x) {return [x.name, undefined]});
                    _internalSlotsData = Immutable.OrderedMap(tmp);
                    _internalNavigationData = data.navigation;
                    _internalNavigationData.currentTime = data.currentTime;
                    console.log("new state", _internalSlotsData.toJS());
                    SlotsStore.emit(CHANGE_EVENT);
                }).bind(this)
            );
            break;

        // add more cases for other actionTypes, like TODO_DESTROY, etc.
    }

    return true; // No errors. Needed by promise in Dispatcher.
});
