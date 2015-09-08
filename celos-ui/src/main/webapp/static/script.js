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
