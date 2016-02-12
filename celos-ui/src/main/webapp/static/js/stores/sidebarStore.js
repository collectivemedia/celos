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

var SidebarRecord = Immutable.Record({
    selectedSlots: null,
    inFocus: null
});

var _internalSidebarData = SidebarRecord({selectedSlots: Immutable.Seq()});

var SidebarStore = Object.assign({}, EventEmitter.prototype, {

    /**
     * Get the entire collection of TODOs.
     * @return {object}
     */
    getAll: function () {
        return _internalSidebarData
    }

});


AppDispatcher.register(function (payload) {

    switch (payload.actionType) {

        case TodoConstants.FOCUS_ON_SLOT:
            var newState = Immutable.fromJS(
                SlotRecord({
                    ts: payload.params.ts,
                    breadcrumbs: payload.params.breadcrumbs,
                    workflowName: payload.params.workflowName
                })
            );
            _internalSidebarData = _internalSidebarData.set("inFocus", newState);
            _internalSidebarData = _internalSidebarData.set("selectedSlots", SlotsStore.getSelectedSlots());
            SidebarStore.emit(CHANGE_EVENT);
            break;

        // add more cases for other actionTypes, like TODO_DESTROY, etc.
    }

    return true; // No errors. Needed by promise in Dispatcher.
});
