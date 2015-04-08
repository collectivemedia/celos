define(['app/slots-view', 'app/managers', 'app/constants', 'app/utils'], function(SlotsView, Managers, Const, Utils) {

    function MainView(slotStatesCanvas, datesViewCanvas, detailsViewCanvas) {

        var _this = this;

        var mutableDims = false;
        var rrmanModel = false;

        var selectionManager = new Managers.SelectManager();
        var zoomer = new Managers.ZoomManager(this);
        var initialCanvasSizeUpdate = false;

        var slotsView = new SlotsView(slotStatesCanvas, datesViewCanvas, detailsViewCanvas, _this);

        this.getSelectionManager = function() {
            return selectionManager;
        };

        this.getZoomer = function() {
            return zoomer;
        };

        this.getMutableDims = function() {
            return mutableDims;
        }

        this.setModel = function (_model) {
            resetSelectedMarkers(_model);
            rrmanModel = _model;
        };

        this.getModel = function() {
            return rrmanModel;
        }

        this.repaint = function () {
            if (!initialCanvasSizeUpdate) {
                slotsView.updateCanvasSize();
                initialCanvasSizeUpdate = true;
            }
            _this.updateMutableDimensions();
            slotsView.repaint();
        };


        this.updateMutableDimensions = function() {
            var maxWidth = Utils.getTextWidth(Const.SAMPLE_DATE, Const.dateTextFontSize);

            for (var i = 0; i < rrmanModel.rrmanConfig.workflowToSlotMap.length; i++) {
                var wfGroup = rrmanModel.rrmanConfig.workflowToSlotMap[i];
                maxWidth = Math.max(maxWidth, Utils.getTextWidth(wfGroup.name, Const.workflowGroupNameFontSize));
                for (var j = 0; j < wfGroup.workflows.length; j++) {
                    maxWidth = Math.max(maxWidth, Utils.getTextWidth(wfGroup.workflows[j], Const.workflowNameFontSize));
                }
            }

            mutableDims = {
                workflowNameWidth: maxWidth,
                cellDataOffset: maxWidth + 2 * Const.workflowNameMargin
            }
        };


        this.getMetricsByDate = function(date) {
            var currentDate = _this.getZoomer().getPagingOffsetDate();
            var millisInPixel = zoomer.getCurrentZoom().baseCellDuration / Const.baseCellWidth;
            var relativeX = (currentDate.getTime() - date.getTime()) / millisInPixel;
            var absoluteX = relativeX + mutableDims.cellDataOffset;
            var leftCut = Math.abs(Math.min(relativeX, 0));

            return {
                millisInPixel: millisInPixel,
                relativeX: relativeX,
                absoluteX: absoluteX,
                leftCut: leftCut
            }
        };


        this.onResize = function () {
            slotsView.onResize();
        };


        function resetSelectedMarkers(_model) {
            function collectSelection(slotState) {
                if (selectionManager.containsSlot(slotState)) {
                    slotState.selected = true;
                    selectionManager.addSlot(slotState);
                }
            }

            _model.workflowToSlotMap.forEach(function (val, key) {
                val.forEach(collectSelection);
            });
        }

        this.triggerUpdate = function () {
            $(document).trigger("rrman:request_data", _this.getZoomer().getPagingOffsetDate());
        };

        this.setupEventListeners = function () {
            slotsView.setupEventListeners();
        };
    }

    return MainView;

});

