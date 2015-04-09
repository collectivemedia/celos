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
        var pagingOffsetDate = null;

        this.getCurrentZoom = function () {
            return zoomers[currentZoomerIndex];
        };

        this.getPagingOffsetDate = function () {
            //return new Date('2013-12-02 19:23 UTC');
            if (pagingOffsetDate) {
                return pagingOffsetDate;
            }
            return new Date();
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

        this.nextPage = function () {
            var cellNum = view.getWidthInCells();
            var newDate = zoomers[currentZoomerIndex].getDateAfterSteps(_this.getPagingOffsetDate(), cellNum - 1);
            _this.gotoDate(newDate);
        };

        this.prevPage = function () {
            var cellNum = view.getWidthInCells();
            var newDate = zoomers[currentZoomerIndex].getDateAfterSteps(_this.getPagingOffsetDate(), -(cellNum - 1));
            if (new Date().getTime() - newDate.getTime() < zoomers[currentZoomerIndex].baseCellDuration) {
                newDate = null;
            }
            _this.gotoDate(newDate);
        };

        this.gotoDate = function (newDate) {
            pagingOffsetDate = newDate;
            //view.repaint();

            setTimeout(function () {
                $(document).trigger("rrman:request_data", _this.getPagingOffsetDate());
            }, 50);
        }

    };

    return {
        SelectManager: SelectManager,
        ZoomManager: ZoomManager
    }

});