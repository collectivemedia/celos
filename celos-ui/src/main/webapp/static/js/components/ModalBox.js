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


var ModalBox = React.createClass({
    displayName: "ContextMenu",

    getInitialState: function () {
        return {
            slots: SlotsStore.getSelectedSlots()
        }
    },

    render: function () {

        if (!this.props.store.get("show", false)) {
            return null
        }
        // else
        var callback = this.props.store.get("callback");
        return (
            React.DOM.div({id: "openModal", className: "modalDialog"},
                React.DOM.div(null,
                    React.DOM.h3(null, "Are you shure?"),
                    React.DOM.button({onClick: function() {
                            callback();
                            AppDispatcher.modalBoxAction({show: false})
                        }},"YES"),
                    React.DOM.button({onClick: function() {
                        AppDispatcher.modalBoxAction({show: false})
                    }},"NO")

                )
            )
        )
    }
});
