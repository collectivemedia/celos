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

var CHANGE_EVENT = 'change';

var TodoConstants = {
    LOAD_GROUPS: "LOAD_GROUPS",
    TODO_UPDATE: "TODO_UPDATE",
    RECTANGLE_UPDATE: "RECTANGLE_UPDATE",
    LOAD_SLOTS: "LOAD_SLOTS",
    FOCUS_ON_SLOT: "FOCUS_ON_SLOT",
    CLEAR_SELECTION: "CLEAR_SELECTION",
    LOAD_NAVIGATION: "LOAD_NAVIGATION",
    MODAL_BOX: "MODAL_BOX"
};

var AppDispatcher = {

    _callbacks: [],
    _promises: [],

    /**
     * A bridge function between the views and the dispatcher, marking the action
     * as a view action.  Another variant here could be handleServerAction.
     * @param  {object} action The data coming from the view.
     */
    focusOnSlot: function (action) {
        console.log("focusOnSlot", action.breadcrumbs + "");
        this.dispatch({
            source: TodoConstants.FOCUS_ON_SLOT,
            action: action,
            breadcrumbs: action.breadcrumbs
        })
    },

    markSlotAsSelected: function (action) {
        console.log("markSlotAsSelected", action.breadcrumbs + "");
        this.dispatch({
            source: TodoConstants.TODO_UPDATE,
            action: action,
            breadcrumbs: action.breadcrumbs
        })
    },

    rectangleSlotSelection: function (action) {
        console.log("markSlotAsSelected", action.breadcrumbs + "");
        this.dispatch({
            source: TodoConstants.RECTANGLE_UPDATE,
            action: action,
            breadcrumbs: action && action.breadcrumbs
        })
    },

    clearSelection: function (action) {
        console.log("clearSelection", action);
        this.dispatch({
            source: TodoConstants.CLEAR_SELECTION,
            breadcrumbs: action && action.breadcrumbs
        })
    },

    handleLoadSlotsFromServer: function (action) {
        console.log("handleLoadSlotsFromServer", action);
        this.dispatch({
            source: TodoConstants.LOAD_SLOTS,
            action: action,
            breadcrumbs: action.breadcrumbs
        })
    },

    //handleLoadGroupsFromServer: function (action) {
    //    console.log("handleLoadGroupsFromServer", action);
    //    this.dispatch({
    //        source: TodoConstants.LOAD_GROUPS
    //    });
    //},

    handleLoadNavigation: function (action) {
        console.log("handleLoadNavigation", action);
        this.dispatch({
            source: TodoConstants.LOAD_NAVIGATION,
            action: action,
            breadcrumbs: action.breadcrumbs
        })

    },

    killSelectedSlots: function () {
        console.log("KILL ACTION");
        ajaxPostJSON("/multiplex-rpc", {
                action: "kill",
                slots: SlotsStore.getSelectedSlots()
            },
            function (res) { console.log(res)}
        )
    },

    rerunSelectedSlots: function () {
        console.log("RERUN ACTION");
        ajaxPostJSON("/multiplex-rpc", {
                action: "rerun",
                slots: SlotsStore.getSelectedSlots()
            },
            function (res) { console.log(res)}
        )
    },

    modalBoxAction: function (action) {
        console.log("modalBoxAction");
        this.dispatch({
            source: TodoConstants.MODAL_BOX,
            action: action
        })
    },

    /**
     * Register a Store's callback so that it may be invoked by an action.
     * @param {function} callback The callback to be registered.
     * @return {number} The index of the callback within the _callbacks array.
     */
    register: function(callback) {
        this._callbacks.push(callback);
        return this._callbacks.length - 1; // index
    },

    /**
     * dispatch
     * @param  {object} payload The data from the action.
     */
    dispatch: function (payload) {
        console.log("AppDispatcher.dispatch", payload);
        // First create array of promises for callbacks to reference.
        var resolves = [];
        var rejects = [];
        this._promises = this._callbacks.map(function(_, i) {
            return new Promise(function(resolve, reject) {
                resolves[i] = resolve;
                rejects[i] = reject;
            });
        });
        // Dispatch to callbacks and resolve/reject promises.
        this._callbacks.forEach(function(callback, i) {
            // Callback can return an obj, to resolve, or a promise, to chain.
            // See waitFor() for why this might be useful.
            Promise.resolve(callback(payload)).then(function() {
                resolves[i](payload);
            }, function() {
                rejects[i](new Error('Dispatcher callback unsuccessful'));
            });
        });
        this._promises = [];
    }

};
