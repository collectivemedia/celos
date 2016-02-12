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
    focusOnSlot: function (params) {
        this.dispatch({
            actionType: TodoConstants.FOCUS_ON_SLOT,
            params: params,
            breadcrumbs: params.breadcrumbs
        })
    },

    markSlotAsSelected: function (params) {
        this.dispatch({
            actionType: TodoConstants.TODO_UPDATE,
            params: params,
            breadcrumbs: params.breadcrumbs
        })
    },

    rectangleSlotSelection: function (params) {
        this.dispatch({
            actionType: TodoConstants.RECTANGLE_UPDATE,
            params: params,
            breadcrumbs: params && params.breadcrumbs
        })
    },

    clearSelection: function (action) {
        this.dispatch({
            actionType: TodoConstants.CLEAR_SELECTION,
            breadcrumbs: action && action.breadcrumbs
        })
    },

    handleLoadSlotsFromServer: function (params) {
        this.dispatch({
            actionType: TodoConstants.LOAD_SLOTS,
            params: params,
            breadcrumbs: params.breadcrumbs
        })
    },

    handleLoadNavigation: function (params) {
        this.dispatch({
            actionType: TodoConstants.LOAD_NAVIGATION,
            params: params,
            breadcrumbs: params.breadcrumbs
        })

    },

    killSelectedSlots: function () {
        ajaxPostJSON("/multiplex-rpc", {
                params: "kill",
                slots: SlotsStore.getSelectedSlots().toJS()
            },
            function (res) { }
        )
    },

    rerunSelectedSlots: function () {
        ajaxPostJSON("/multiplex-rpc", {
                params: "rerun",
                slots: SlotsStore.getSelectedSlots().toJS()
            },
            function (res) { }
        )
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
        console.log("EVENT: " + payload.source + " -- " + JSON.stringify(payload.breadcrumbs ? payload.breadcrumbs.toJS() : undefined));
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
