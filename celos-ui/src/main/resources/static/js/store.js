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

var AppDispatcher = {

    clickOnSlot: function (breadcrumb) {
        if (slotTimestamp !== null) {
            var newSlots = this.state.selectedSlots.updateIn([wfName, slotTimestamp, "isSelected"], false, function (x) {
                return !x;
            });
            this.setState({
                selectedSlots: newSlots
            })
        }
    },


    register: function (callback) {


    }

};

var CHANGE_EVENT = 'change';

var TodoConstants = {
    TODO_CREATE: "TODO_CREATE",
    TODO_UPDATE: "TODO_UPDATE",
    TODO_DESTROY: "TODO_DESTROY"
};

var WorkflowStore = {

    internalData: Immutable.Map(),

    internalEvents: {},

    /**
     * Get the entire collection of TODOs.
     * @return {object}
     */
    getAll: function() {
        return this.internalData;
    },

    emitChange: function() {
        this.internalEvents[CHANGE_EVENT].forEach(function(callback) {
            callback()
        })
    },

    /**
     * @param {function} callback
     */
    addChangeListener: function (callback) {
        this.internalEvents[CHANGE_EVENT].push(callback)
    },

    /**
     * @param {function} callback
     */
    removeChangeListener: function (callback) {
        this.internalEvents[CHANGE_EVENT] = this.internalEvents[CHANGE_EVENT]
            .filter(function(x) {return x !== callback});
    },

    dispatcherIndex: AppDispatcher.register(function (payload) {

        switch(payload.action.actionType) {
            case TodoConstants.TODO_UPDATE:
                this.internalData.setIn(payload.action.breadcrumb, payload.action.status);
                TodoStore.emitChange();
                break;

            // add more cases for other actionTypes, like TODO_DESTROY, etc.
        }

        return true; // No errors. Needed by promise in Dispatcher.
    })

};
