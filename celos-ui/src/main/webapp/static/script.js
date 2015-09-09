/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
var celos = {
    load: function() {
        $("a.slotLink").click(function(event) {
            if (event.shiftKey) {
                event.preventDefault();
                var slotID = $(event.target).data("slot-id");
                $.ajax({
                    type: "POST",
                    url: "rerun?id=" + encodeURIComponent(slotID),
                    success: function() { $(event.target).html("rrun"); },
                    error: function(jqXHR, textStatus, errorThrown) {
                        alert("Error when rerunning slot " + slotID + ": " + textStatus + " (check console)");
                        console.log(errorThrown);
                    }
                });
            }
        });
    }
}
$(document).ready(celos.load);
