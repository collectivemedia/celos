define(["app/utils", "app/constants", "lib/immutable"], function (Utils, Const, Immutable) {

    var SelectManager = function () {
        var selectedSlots = new Immutable.Map();

        this.addSlot = function (slot) {
            selectedSlots = selectedSlots.set(slot.getSlotID(), slot);
        };

        this.removeSlot = function (slot) {
            selectedSlots = selectedSlots.delete(slot.getSlotID());
        };

        this.containsSlot = function (slot) {
            return selectedSlots.has(slot.getSlotID());
        };

        this.slotsIterator = function () {
            return selectedSlots.values();
        };
    };

    var ZoomManager = function (view) {

        function ZoomView(baseCellDuration, descFunc) {
            this.baseCellDuration = baseCellDuration;
            this.getDescription = descFunc;
        }


        ZoomView.prototype.getDateAfterSteps = function (date, steps) {
            var newDate = new Date(date);
            newDate.setUTCMinutes(newDate.getUTCMinutes() - (this.baseCellDuration / Const.MINUTE_MS) * steps);
            return newDate;
        };

        var _this = this;
        var zoomers = [
            new ZoomView(4 * Const.HOUR_MS, function (date) {
                return Utils.getPaddedUTCHours(date) + ":00";
            }),
            new ZoomView(2 * Const.HOUR_MS, function (date) {
                return Utils.getPaddedUTCHours(date) + ":00";
            }),
            new ZoomView(Const.HOUR_MS, function (date) {
                return Utils.getPaddedUTCHours(date) + ":00";
            }),
            new ZoomView(30 * Const.MINUTE_MS, function (date) {
                return Utils.getPaddedUTCHours(date) + ":" + Utils.getPaddedUTCMinutes(date);
            }),
            new ZoomView(15 * Const.MINUTE_MS, function (date) {
                return Utils.getPaddedUTCHours(date) + ":" + Utils.getPaddedUTCMinutes(date);
            }),
            new ZoomView(5 * Const.MINUTE_MS, function (date) {
                return Utils.getPaddedUTCHours(date) + ":" + Utils.getPaddedUTCMinutes(date);
            })
        ];

        var currentZoomerIndex = 1;
        var pagingOffsetDate = null;//view.getUrl();

        this.getCurrentZoom = function () {
            return zoomers[currentZoomerIndex];
        };

        this.getCurrentViewDate = function () {
            //return new Date('2013-12-02 19:23 UTC');
            if (pagingOffsetDate) {
                return pagingOffsetDate;
            }
            return new Date();
        };

        this.getPagingOffsetDate = function () {
            return pagingOffsetDate;
        };

        this.zoomIn = function () {
            if (currentZoomerIndex < zoomers.length - 1) {
                currentZoomerIndex++;
            }
            view.repaintAll();
        };

        this.zoomOut = function () {
            if (currentZoomerIndex > 0) {
                currentZoomerIndex--;
            }
            view.repaintAll();
        };

        this.setDateNextPage = function () {
            var cellNum = view.getWidthInCells();
            var newDate = zoomers[currentZoomerIndex].getDateAfterSteps(_this.getCurrentViewDate(), cellNum - 1);
            _this.setDate(newDate);
        };

        this.setDatePrevPage = function () {
            var cellNum = view.getWidthInCells();
            var newDate = zoomers[currentZoomerIndex].getDateAfterSteps(_this.getCurrentViewDate(), -(cellNum - 1));
            if (new Date().getTime() - newDate.getTime() < zoomers[currentZoomerIndex].baseCellDuration) {
                newDate = null;
            }
            _this.setDate(newDate);
        };

        this.setDate = function(newDate) {
            pagingOffsetDate = newDate;
        }

    };

    return {
        SelectManager: SelectManager,
        ZoomManager: ZoomManager
    }

});