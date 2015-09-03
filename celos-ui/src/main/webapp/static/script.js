var celos = {
    load: function() {
        $("a.slotLink").click(function(event) {
            if (event.altKey) {
                event.preventDefault();
                alert(event.target.data("slot-id"));
            }
        });
    }
}
$(document).ready(celos.load);
