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
    LOAD_SLOTS: "LOAD_SLOTS",
    SIDEBAR_UPDATE: "SIDEBAR_UPDATE"
};

var AppDispatcher = {

    _callbacks: [],
    _promises: [],

    /**
     * A bridge function between the views and the dispatcher, marking the action
     * as a view action.  Another variant here could be handleServerAction.
     * @param  {object} action The data coming from the view.
     */
    handleClickOnSlot: function (action) {
        console.log("handleClickOnSlot", action.breadcrumbs + "");
        this.dispatch({
            source: TodoConstants.SIDEBAR_UPDATE,
            action: action,
            breadcrumbs: action.breadcrumbs
        })
    },

    handleSelectSlot: function (action) {
        console.log("handleSelectSlot", action.breadcrumbs + "");
        this.dispatch({
            source: TodoConstants.TODO_UPDATE,
            action: action,
            breadcrumbs: action.breadcrumbs
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

    handleLoadGroupsFromServer: function (action) {
        console.log("handleLoadGroupsFromServer", action);
        this.dispatch({
            source: TodoConstants.LOAD_GROUPS,
            action: action,
            breadcrumbs: action.breadcrumbs
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
