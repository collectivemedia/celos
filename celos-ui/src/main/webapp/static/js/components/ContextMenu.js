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


var ContextMenu = React.createClass({
    displayName: "ContextMenu",

    proceedClick1: function () {
        ajaxPostJSON("/multiplex-rpc", {
                action: "rerun",
                slots: SlotsStore.getSelectedSlots()
            },
            function (res) { console.log(res)}
        )
    },

    proceedClick2: function () {
        ajaxPostJSON("/multiplex-rpc", {
                action: "kill",
                slots: SlotsStore.getSelectedSlots()
            },
            function (res) { console.log(res)}
        )
    },

    render: function () {
        if (!this.props.showElement) {
            return null
        }
        // else
        return (
            React.DOM.ul({className: "dropdown-menu",
                    style: {
                        display: "block",
                        position: "absolute",
                        top: this.props.y,
                        left: this.props.x
                    }},
                React.DOM.li({onClick: this.proceedClick1},
                    React.DOM.a({href: window.location.hash, tabIndex: -1},
                        "Kill workflows")),
                React.DOM.li({onClick: this.proceedClick2},
                    React.DOM.a({href: window.location.hash, tabIndex: -1},
                        "Rerun workflows"))
            )
        )
    }
});

