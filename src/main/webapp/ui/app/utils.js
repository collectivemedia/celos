define(['lib/konva'], function (Konva) {

    function Utils() {

        var _this = this;

        function pad(n) {
            return n < 10 ? '0' + n : n
        }

        this.addMs = function (date, ms) {
            return new Date(date.getTime() + ms);
        };

        this.getPaddedUTCHours = function (date) {
            return pad(date.getUTCHours());
        };

        this.getPaddedUTCMinutes = function (date) {
            return pad(date.getUTCMinutes());
        };

        this.toCelosUTCString = function (date) {
            return date.toISOString().slice(0, 10) + "T" + _this.getPaddedUTCHours(date) + ':' + _this.getPaddedUTCMinutes(date) + ":" + pad(date.getSeconds()) + "Z";
        };

        this.toDayString = function (date) {
            return date.toISOString().slice(0, 10);
        };

        this.getTextWidth = function(text, fontSize) {
            var workflowName = new Konva.Text({text: text, fontSize: fontSize});
            return workflowName.getWidth();
        };

        return this;

    };

    return new Utils();

});