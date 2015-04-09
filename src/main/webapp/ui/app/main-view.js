define(['app/details-view', 'app/dates-view', 'app/slots-view', 'app/managers', 'app/constants', 'app/utils'],
    function(DetailsView, DatesView, SlotsView, Managers, Const, Utils) {

    function MainView(slotStatesCanvas, datesViewCanvas, detailsViewCanvas) {

        var _this = this;

        var mutableDims = false;
        var uiModel = false;

        var selectionManager = new Managers.SelectManager();
        var zoomer = new Managers.ZoomManager(this);
        var initialCanvasSizeUpdate = false;

        var slotsView = new SlotsView(slotStatesCanvas, _this);
        var datesView = new DatesView(datesViewCanvas, _this);
        var detailsView = new DetailsView(detailsViewCanvas, _this);

        this.getSelectionManager = function() {
            return selectionManager;
        };

        this.getZoomer = function() {
            return zoomer;
        };

        this.getMutableDims = function() {
            return mutableDims;
        };

        this.setModel = function (_model) {
            resetSelectedMarkers(_model);
            uiModel = _model;
        };

        this.getModel = function() {
            return uiModel;
        };

        this.repaintAll = function () {
            if (!initialCanvasSizeUpdate) {
                datesView.updateCanvasSize();
                slotsView.updateCanvasSize();
                initialCanvasSizeUpdate = true;
            }
            updateMutableDimensions();
            detailsView.repaint();
            datesView.repaint();
            slotsView.repaint();
        };

        this.getWidthInCells = function () {
            var panelWidth = slotStatesCanvas.stage.width() - _this.getMutableDims().cellDataOffset;
            return Math.floor(panelWidth / Const.baseCellWidth);
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
            datesView.onResize();
            slotsView.onResize();
        };

        function updateMutableDimensions() {
            var maxWidth = Utils.getTextWidth(Const.SAMPLE_DATE, Const.dateTextFontSize);

            for (var i = 0; i < uiModel.uiConfig.workflowToSlotMap.length; i++) {
                var wfGroup = uiModel.uiConfig.workflowToSlotMap[i];
                maxWidth = Math.max(maxWidth, Utils.getTextWidth(wfGroup.name, Const.workflowGroupNameFontSize));
                for (var j = 0; j < wfGroup.workflows.length; j++) {
                    maxWidth = Math.max(maxWidth, Utils.getTextWidth(wfGroup.workflows[j], Const.workflowNameFontSize));
                }
            }

            mutableDims = {
                workflowNameWidth: maxWidth,
                cellDataOffset: maxWidth + 2 * Const.workflowNameMargin
            }
        }

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

            function showCellDetails(slotState) {
                $('#bottomPanel').text(slotState.getDescription());
            }

            function clearCellDetails() {
                $('#bottomPanel').text('');
            }

            slotStatesCanvas.stage.on('mouseover mousemove dragmove', function (evt) {
                if (evt.target && evt.target.slotState) {
                    showCellDetails(evt.target.slotState);
                } else {
                    clearCellDetails();
                }
            });
            slotStatesCanvas.stage.on('mouseout', function (evt) {
                if (evt.target && evt.target.slotState) {
                    clearCellDetails();
                }
            });
            slotStatesCanvas.stage.on('click', function (evt) {
                if (!evt.target || !evt.target.slotState) {
                    return;
                }
                var slotState = evt.target.slotState;

                slotState.selected = !slotState.selected;
                if (slotState.selected) {
                    _this.getSelectionManager().addSlot(slotState);
                } else {
                    _this.getSelectionManager().removeSlot(slotState);
                }
                slotState.repaint();
                detailsView.repaint();
            });

            document.getElementById("zoomIn").addEventListener("click", _this.getZoomer().zoomIn);
            document.getElementById("zoomOut").addEventListener("click", _this.getZoomer().zoomOut);
            document.getElementById("nextPage").addEventListener("click", _this.getZoomer().nextPage);
            document.getElementById("prevPage").addEventListener("click", _this.getZoomer().prevPage);
            document.getElementById("gotoDate").addEventListener("click", function () {
                var newDate = Utils.addMs(new Date(document.getElementById("datepicker").value), Const.DAY_MS);
                _this.getZoomer().gotoDate(newDate);
            });
            document.getElementById("rerunSelected").addEventListener("click", function () {
                var iter = _this.getSelectionManager().slotsIterator();
                var next = iter.next();
                while (!next.done) {
                    var slot = next.value;
                    if (slot.canBeRestarted()) {
                        $(document).trigger("rrman:slot_rerun", slot);
                    }
                    slot.selected = false;
                    slot.repaint();
                    _this.getSelectionManager().removeSlot(slot);
                    next = iter.next();
                }
                detailsView.repaint();
            });

        };
    }

    return MainView;

});

