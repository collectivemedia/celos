var SECOND_MS = 1000;
var MINUTE_MS = 60 * SECOND_MS;
var HOUR_MS = 60 * MINUTE_MS;
var DAY_MS = 24 * HOUR_MS;
var SAMPLE_DATE = '2015-02-05 13:23 UTC';
var SAMPLE_DATE_DAY = '2015-02-05';
var SAMPLE_LINE_TIME = '13:23';
var REFRESH_INTERVAL = 5000;

var STATUSES = {};

function addStatus(name, color, selectedColor, abbreviation) {
    function Status(name, color) {
        this.name = name;
        this.color = color;
        this.selectedColor = selectedColor;
        this.abbrevation = abbreviation;
        this.abbrevationWidth = getTextWidth(abbreviation, UI_PROPERTIES.slotStateAbbreviationFontSize);
    }

    STATUSES[name] = new Status(name, color);
}


var UI_PROPERTIES = {
    ZOOM_TOO_LOW_STROKE_MARGIN: 5,
    ZOOM_TOO_LOW_STROKE_STEP: 10,
    workflowNameMargin: 20,
    workflowNameYOffset: 8,
    workflowGroupNameFontSize: 18,
    workflowNameFontSize: 15,
    slotStateAbbreviationFontSize: 15,
    dateTextFontSize: 17,
    baseCellWidth: 69,
    cellHeight: 36,
    scrollbarWidth: 20,
    cellMinimalWidth: 5,
    cellColorZoomMe: '#bbb',
    zoomInDesrcText: 'Zoom in to see Slots'
};

function SlotState(status, workflow, date, externalID, retryCount) {
    this.status = status;
    this.workflow = workflow;
    this.date = date;
    this.externalID = externalID;
    this.retryCount = retryCount;
    this.selected = false;
}

SlotState.prototype.getSlotID = function() {
    return this.workflow.id + '@' + this.date.toISOString();
};

SlotState.prototype.getDescription = function() {
    return 'Slot ' + this.getSlotID() + ': ' + this.status.name;
};

SlotState.prototype.getColor = function() {
    if (this.selected) {
        return this.status.selectedColor;
    } else {
        return this.status.color;
    }
};

SlotState.prototype.repaint = function() {
    if (this.rect) {
        this.rect.setFill(this.getColor());
        this.rect.draw();
    }
    if (this.abbr) {
        this.abbr.setText(this.status.abbrevation);
        this.abbr.draw();
    }
};

SlotState.prototype.canBeRestarted = function() {
    return (this.status == STATUSES['FAILURE'] || this.status == STATUSES['SUCCESS']);
};

function WorkflowGroup(name, workflowToSlotsMap) {
    this.name = name;
    this.workflowToSlotsMap = workflowToSlotsMap;
}

function Workflow(id, info) {
    this.id = id;
    this.info = info;
}

Workflow.prototype.getDescription = function() {
    return 'Workflow ' + this.id;
};


function RrmanModel(rrmanConfig, workflowToSlotMap) {
    this.rrmanConfig = rrmanConfig;
    this.workflowToSlotMap = workflowToSlotMap;
    var idsToWf = workflowToSlotMap.reduce(function(accum, slots, workflow) {
        accum.push([workflow.id, workflow]);
        return accum;
    }, []);
    this.workflowByName = new Immutable.Map(idsToWf);
}


var DateUtils = (function(){

    var _this = this;

    function pad(n){
        return n < 10 ? '0' + n : n
    }

    this.addMs = function(date, ms) {
        return new Date(date.getTime() + ms);
    };

    this.getPaddedUTCHours = function(date){
        return pad(date.getUTCHours());
    };

    this.getPaddedUTCMinutes = function(date){
        return pad(date.getUTCMinutes());
    };

    this.toCelosUTCString = function(date) {
        return date.toISOString().slice(0, 10) + "T" + _this.getPaddedUTCHours(date) + ':' + _this.getPaddedUTCMinutes(date) + ":" + pad(date.getSeconds()) + "Z";
    };

    this.toDayString = function(date) {
        return date.toISOString().slice(0, 10);
    };


    return this;

})();

var SelectManager = function() {
    var selectedSlots = new Immutable.Map();

    this.addSlot = function(slot) {
        selectedSlots = selectedSlots.set(slot.getSlotID(), slot);
    };

    this.removeSlot = function(slot) {
        selectedSlots = selectedSlots.delete(slot.getSlotID());
    };

    this.containsSlot = function(slot) {
        return selectedSlots.has(slot.getSlotID());
    };

    this.slotsIterator = function() {
        return selectedSlots.values();
    };
};

var ZoomManager = function(view) {

    function ZoomView(baseCellDuration, descFunc) {
        this.baseCellDuration = baseCellDuration;
        this.getDescription = descFunc;
    }


    ZoomView.prototype.getDateAfterSteps = function(date, steps) {
        var newDate = new Date(date);
        newDate.setUTCMinutes(newDate.getUTCMinutes() - (this.baseCellDuration / MINUTE_MS) * steps);
        return newDate;
    };

    var _this = this;
    var zoomers = [
        new ZoomView(4 * HOUR_MS, function(date) { return DateUtils.getPaddedUTCHours(date) + ":00"; }),
        new ZoomView(2 * HOUR_MS, function(date) { return DateUtils.getPaddedUTCHours(date) + ":00"; }),
        new ZoomView(HOUR_MS, function(date) { return DateUtils.getPaddedUTCHours(date) + ":00"; }),
        new ZoomView(30 * MINUTE_MS, function(date) { return DateUtils.getPaddedUTCHours(date) + ":" + DateUtils.getPaddedUTCMinutes(date); }),
        new ZoomView(15 * MINUTE_MS, function(date) { return DateUtils.getPaddedUTCHours(date) + ":" + DateUtils.getPaddedUTCMinutes(date); }),
        new ZoomView(5 * MINUTE_MS, function(date) { return DateUtils.getPaddedUTCHours(date) + ":" + DateUtils.getPaddedUTCMinutes(date); })
    ];

    var currentZoomerIndex = 1;
    var pagingOffsetDate = null;

    this.getCurrentZoom = function() {
        return zoomers[currentZoomerIndex];
    };

    this.getPagingOffsetDate = function() {
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
        view.repaint();
    };

    this.zoomOut = function () {
        if (currentZoomerIndex > 0) {
            currentZoomerIndex--;
        }
        view.repaint();
    };

    this.nextPage = function() {
        var cellNum = view.getCellsNumber();
        var newDate = zoomers[currentZoomerIndex].getDateAfterSteps(_this.getPagingOffsetDate(), cellNum - 1);
        _this.gotoDate(newDate);
    };

    this.prevPage = function () {
        var cellNum = view.getCellsNumber();
        var newDate = zoomers[currentZoomerIndex].getDateAfterSteps(_this.getPagingOffsetDate(), -(cellNum - 1));
        if (new Date().getTime() - newDate.getTime() < zoomers[currentZoomerIndex].baseCellDuration) {
            newDate = null;
        }
        _this.gotoDate(newDate);
    };

    this.gotoDate = function(newDate) {
        pagingOffsetDate = newDate;
        //view.repaint();

        setTimeout(function() {
            $(document).trigger("rrman:request_data", _this.getPagingOffsetDate());
        }, 50);
    }

};

function init() {

    addStatus('READY', '#ffc', '#ffa', 'rdy');
    addStatus('RUNNING', '#ffc', '#ffa', 'run');
    addStatus('SUCCESS', '#cfc', '#afa', 'succ');
    addStatus('FAILURE', '#fcc', '#faa', 'fail');
    addStatus('WAITING', '#ccf', '#aaf', 'wait');

    var slotStatesPanel = $("#slotStatesPanel");
    var slotStatesStage = new Konva.Stage({
        container: 'slotStatesPanel',
        width: slotStatesPanel.width() - 20,
        height: slotStatesPanel.height() - 20
    });
    var slotStatesLayerActive = new Konva.Layer();
    var slotStatesLayerInactive = new Konva.Layer();
    slotStatesStage.add(slotStatesLayerActive);
    slotStatesStage.add(slotStatesLayerInactive);

    var datePanel = $("#datePanel");
    var dateStage = new Konva.Stage({
        container: 'datePanel',
        width: datePanel.width(),
        height: datePanel.height()
    });
    var dateLayer = new Konva.Layer();
    dateStage.add(dateLayer);

    var detailsPanel = $("#selectedSlotsDetailsPanel");
    var detailsStage = new Konva.Stage({
        container: 'selectedSlotsDetailsPanel',
        width: detailsPanel.width() - 20,
        height: detailsPanel.height() - 20
    });
    var detailsLayer = new Konva.Layer();
    detailsStage.add(detailsLayer);


    var slotStatesContext = { activeLayer: slotStatesLayerActive, inactiveLayer: slotStatesLayerInactive, stage: slotStatesStage, panel: slotStatesPanel };
    var dateContext = { layer: dateLayer, stage: dateStage, panel: datePanel };
    var detailsContext = { layer: detailsLayer, stage: detailsStage, panel: detailsPanel };


    var rrmanView = new RrmanView(slotStatesContext, dateContext, detailsContext);
    var client = new RrmanServerClient(rrmanView);

    $(document).on( "rrman:model_updated", function(evt, model) {
        rrmanView.setModel(model);
        rrmanView.repaint();
    });
    $(document).on( "rrman:request_data", function(evt, date) {
        client.update(date);
    });
    $(document).on( "rrman:slot_rerun", function(evt, slot) {
        client.rerunSlot(slot);
    });

    rrmanView.setupEventListeners();
    rrmanView.triggerUpdate();

    setInterval(function () {
        rrmanView.triggerUpdate();
    }, REFRESH_INTERVAL);

    $(function() {
        $( "#datepicker" ).datepicker();
        $( "button" ).button();
    });
}


function showCellDetails(slotState) {
    $('#bottomPanel').text(slotState.getDescription());
}

function clearCellDetails() {
    $('#bottomPanel').text('');
}

function getTextWidth(text, fontSize) {
    var workflowName = new Konva.Text({text: text, fontSize: fontSize});
    return workflowName.getWidth();
}

function RrmanView(slotStatesView, datesView, detailsView) {

    var _this = this;
    var mutableDims;
    var rrmanModel;

    var selectionManager = new SelectManager();
    var zoomer = new ZoomManager(this);
    var initialCanvasSizeUpdate = false;

    (function() {
        var aText = new Konva.Text({
            x: slotStatesView.stage.width() / 2,
            y: slotStatesView.stage.height() / 2,
            backgroundColor: 'green',
            align: 'center',
            text: "Please wait for data...",
            fontSize: 40, fontStyle: 'bold'
        });
        slotStatesView.activeLayer.add(aText);
        slotStatesView.activeLayer.batchDraw();
    })();

    this.repaint = function() {
        if (!initialCanvasSizeUpdate) {
            _this.updateCanvasSize();
            initialCanvasSizeUpdate = true;
        }
        updateMutableDimensions();
        repaintDatePanel();
        repaintSlotStatePanel();
        repaintDetailsPanel();
    };

    this.triggerUpdate = function() {
        $(document).trigger("rrman:request_data", _this.getCurrentDate());
    };

    this.setupEventListeners = function() {
        slotStatesView.stage.on('mouseover mousemove dragmove', function(evt) {
            if (evt.target && evt.target.slotState) {
                showCellDetails(evt.target.slotState);
            } else {
                clearCellDetails();
            }
        });
        slotStatesView.stage.on('mouseout', function(evt) {
            if (evt.target && evt.target.slotState) {
                clearCellDetails();
            }
        });
        slotStatesView.stage.on('click', function(evt) {
            if (!evt.target || !evt.target.slotState) {
                return;
            }
            var slotState = evt.target.slotState;

            slotState.selected = !slotState.selected;
            if (slotState.selected) {
                selectionManager.addSlot(slotState);
            } else {
                selectionManager.removeSlot(slotState);
            }
            slotState.repaint();
            repaintDetailsPanel();
        });

        document.getElementById("zoomIn").addEventListener("click", zoomer.zoomIn);
        document.getElementById("zoomOut").addEventListener("click", zoomer.zoomOut);
        document.getElementById("nextPage").addEventListener("click", zoomer.nextPage);
        document.getElementById("prevPage").addEventListener("click", zoomer.prevPage);
        document.getElementById("gotoDate").addEventListener("click", function() {
            var newDate = DateUtils.addMs(new Date(document.getElementById("datepicker").value), DAY_MS);
            zoomer.gotoDate(newDate);
        });
        document.getElementById("rerunSelected").addEventListener("click", function() {
            var iter = selectionManager.slotsIterator();
            var next = iter.next();
            while (!next.done) {
                var slot = next.value;
                if (slot.canBeRestarted()) {
                    $(document).trigger("rrman:slot_rerun", slot);
                }
                slot.selected = false;
                slot.repaint();
                selectionManager.removeSlot(slot);
                next = iter.next();
            }
            repaintDetailsPanel();
        });
        var onResize = function() {
            _this.updateCanvasSize();
            _this.repaint()
        };
        window.addEventListener('resize', onResize, false);

        //var m = 0;
        //window.addEventListener('resize', function() {console.log(m++)}, false);
        //$('input').bind('keyup blur', $.debounce(process, 300));
    };

    this.updateCanvasSize = function() {
        slotStatesView.stage.setWidth(slotStatesView.panel.width() - UI_PROPERTIES.scrollbarWidth);
        datesView.stage.setWidth(slotStatesView.panel.width() - UI_PROPERTIES.scrollbarWidth);
    };

    this.getCurrentDate = function() {
        return zoomer.getPagingOffsetDate();
    };


    this.setModel = function(_model) {
        resetSelectedMarkers(_model);
        rrmanModel = _model;
    };

    this.getCellsNumber = function () {
        var datesPanelWidth = datesView.stage.width() - mutableDims.cellDataOffset;
        return Math.floor(datesPanelWidth / UI_PROPERTIES.baseCellWidth);
    };

    function getMetricsByDate(date) {
        var millisInPixel = zoomer.getCurrentZoom().baseCellDuration / UI_PROPERTIES.baseCellWidth;
        var relativeX = (_this.getCurrentDate().getTime() - date.getTime()) / millisInPixel;
        var absoluteX = relativeX + mutableDims.cellDataOffset;
        var leftCut = Math.abs(Math.min(relativeX, 0));

        return {
            millisInPixel: millisInPixel,
            relativeX: relativeX,
            absoluteX: absoluteX,
            leftCut: leftCut
        }
    }

    function repaintDetailsPanel() {
        detailsView.layer.destroyChildren();

        var selectedSlots = selectionManager.slotsIterator();
        var next = selectedSlots.next();

        var x = 0;
        var y = 0;
        var newHeight = 0;
        while (!next.done) {
            var slotState = next.value;
            var properties = {
                x: x, y: y, textColor: 'red', backgroundColor: 'green',
                width: detailsView.stage.getWidth(), align: 'left', text: slotState.getSlotID(),
                fontSize: 16, fontStyle: 'bold'
            };
            if (slotState.externalID) {
                properties.fill = 'blue';
                properties.textDecoration = 'underline';
            }
            var aText = new Konva.Text(properties);
            if (slotState.externalID) {
                aText.on('mouseover', function() {
                    document.body.style.cursor = 'pointer';
                });
                aText.on('mouseout', function() {
                    document.body.style.cursor = 'default';
                });
                aText.on('click', function() {
                    window.open('http://www.google.com', '_blank');
                });
            }
            detailsView.layer.add(aText);
            next = selectedSlots.next();
            y += aText.getHeight();
            newHeight = y;
        }
        detailsView.stage.setHeight(newHeight);
        detailsView.layer.batchDraw();

    }

    function resetSelectedMarkers(_model) {
        function collectSelection(slotState) {
            if (selectionManager.containsSlot(slotState)) {
                slotState.selected = true;
                selectionManager.addSlot(slotState);
            }
        }
        _model.workflowToSlotMap.forEach(function(val, key) {
            val.forEach(collectSelection);
        });
        var t2 = new Date().getTime();
    }

    function updateMutableDimensions() {
        var maxWidth = getTextWidth(SAMPLE_DATE, UI_PROPERTIES.dateTextFontSize);

        for (var i=0; i < rrmanModel.rrmanConfig.workflowToSlotMap.length; i++) {
            var wfGroup = rrmanModel.rrmanConfig.workflowToSlotMap[i];
            maxWidth = Math.max(maxWidth, getTextWidth(wfGroup.name, UI_PROPERTIES.workflowGroupNameFontSize));
            for (var j=0; j < wfGroup.workflows.length; j++) {
                maxWidth = Math.max(maxWidth, getTextWidth(wfGroup.workflows[j], UI_PROPERTIES.workflowNameFontSize));
            }
        }

        mutableDims = {
            workflowNameWidth: maxWidth,
            cellDataOffset: maxWidth + 2 * UI_PROPERTIES.workflowNameMargin
        }
    }

    function repaintDatePanel() {
        datesView.layer.destroyChildren();
        var dateText = new Konva.Text({
            x: UI_PROPERTIES.workflowNameMargin,
            y: 0,
            align: 'right',
            text: DateUtils.toCelosUTCString(zoomer.getPagingOffsetDate()),
            fontSize: UI_PROPERTIES.dateTextFontSize, fontStyle: 'bold'
        });
        datesView.layer.add(dateText);

        var mainDateWidth = getTextWidth(SAMPLE_DATE_DAY, UI_PROPERTIES.dateTextFontSize);
        var width = getTextWidth(SAMPLE_LINE_TIME, UI_PROPERTIES.dateTextFontSize);

        var current = zoomer.getPagingOffsetDate().getTime();
        var aDate = new Date(current - current % zoomer.getCurrentZoom().baseCellDuration);
        var metrics = getMetricsByDate(aDate);
        var midnightPass = false;
        while (metrics.absoluteX < datesView.stage.width()) {

            if (midnightPass) {
                var midMetrics = getMetricsByDate(midnightPass);
                var x = midMetrics.absoluteX - mainDateWidth / 2;
                if (x > mutableDims.cellDataOffset) {
                    var aText = new Konva.Text({
                        x: x,
                        y: 0,
                        align: 'center',
                        text: DateUtils.toDayString(midnightPass),
                        fontSize: UI_PROPERTIES.dateTextFontSize, fontStyle: 'bold'
                    });
                    datesView.layer.add(aText);
                }
                midnightPass = false;
            }
            var aText = new Konva.Text({
                x: metrics.absoluteX - width / 2,
                y: 20,
                align: 'center',
                text: zoomer.getCurrentZoom().getDescription(aDate),
                fontSize: UI_PROPERTIES.dateTextFontSize, fontStyle: 'bold'
            });
            var line = new Konva.Line({
                points: [metrics.absoluteX, 40, metrics.absoluteX, dateText.getWidth()],
                strokeWidth: 1,
                stroke: 'black'
            });
            datesView.layer.add(line);
            datesView.layer.add(aText);
            var newDate = zoomer.getCurrentZoom().getDateAfterSteps(aDate, 1);
            if (newDate.getUTCDay() != aDate.getUTCDay()) {
                midnightPass = aDate;
            }
            aDate = newDate;
            metrics = getMetricsByDate(aDate);
        }

        datesView.layer.batchDraw();
    }

    function repaintSlotStatePanel() {

        var oldLayer = slotStatesView.activeLayer;
        slotStatesView.activeLayer = slotStatesView.inactiveLayer;
        slotStatesView.inactiveLayer = oldLayer;

        var yOffset = 0;

        for (var i=0; i < rrmanModel.rrmanConfig.workflowToSlotMap.length; i++) {
            var wfGroup = rrmanModel.rrmanConfig.workflowToSlotMap[i];
            var workflowGroupName = new Konva.Text({
                x: UI_PROPERTIES.workflowNameMargin,
                y: yOffset + UI_PROPERTIES.workflowNameYOffset,
                align: 'right',
                text: wfGroup.name,
                width: mutableDims.workflowNameWidth,
                fontSize: UI_PROPERTIES.workflowGroupNameFontSize,
                fontStyle: 'bold'
            });
            yOffset += UI_PROPERTIES.cellHeight;
            slotStatesView.activeLayer.add(workflowGroupName);

            for (var j=0; j < wfGroup.workflows.length; j++) {
                var workflow = rrmanModel.workflowByName.get(wfGroup.workflows[j]);
                drawWorkflow(yOffset, wfGroup.workflows[j], rrmanModel.workflowToSlotMap.get(workflow));
                yOffset += UI_PROPERTIES.cellHeight;
            }
            yOffset += UI_PROPERTIES.cellHeight;
        }
        slotStatesView.stage.setHeight(yOffset - UI_PROPERTIES.cellHeight);


        //setTimeout(function() {
        var t1 = new Date().getTime();

            slotStatesView.activeLayer.listening(true);
            //slotStatesView.activeLayer.x(Math.random()*100);
            slotStatesView.activeLayer.setVisible(true);
            slotStatesView.activeLayer.batchDraw();
            var t3 = new Date().getTime();
            slotStatesView.inactiveLayer.listening(false);
            slotStatesView.inactiveLayer.setVisible(false);
            slotStatesView.inactiveLayer.destroyChildren();
            var t4 = new Date().getTime();

        console.log(t4 - t1);
//        console.log("t1 " + (t2 - t1) + " t2 " + (t3 - t2) + " t3 " + (t4 - t3));
//        }, 5);

    }


    function drawWorkflow(yOffset, workflow, slotStatesSeq) {

        var color;
        if (slotStatesSeq) {
            if (!drawSlots(yOffset, slotStatesSeq)) {
                drawSolidSlot(yOffset, slotStatesView.stage.width() - mutableDims.cellDataOffset, workflow);
            }
            color = 'black';
        } else {
            color = 'red';
        }

        var workflowName = new Konva.Text({
            x: UI_PROPERTIES.workflowNameMargin, y: yOffset + UI_PROPERTIES.workflowNameYOffset,
            align: 'right', text: workflow, width: mutableDims.workflowNameWidth,
            fontSize: UI_PROPERTIES.workflowNameFontSize, fill: color
        });
        //slotStatesView.activeLayer.add(rect);
        slotStatesView.activeLayer.add(workflowName);

    }

    function prepareSlots(yOffset, slotStatesMap) {

        var shapes = [];
        var slotStates = slotStatesMap.toArray();

        for (var i = 0; i < slotStates.length; i++) {
            var slotState = slotStates[i];
            var metrics = getMetricsByDate(slotState.date);

            if (metrics.leftCut < UI_PROPERTIES.baseCellWidth && metrics.absoluteX <= slotStatesView.stage.getWidth()) {

                var widthNoCut;
                if (i + 1 < slotStates.length) {
                    var nextSlotMetrics = getMetricsByDate(slotStates[i + 1].date);
                    var widthToNextSlot = nextSlotMetrics.absoluteX - metrics.absoluteX;
                    if (widthToNextSlot < UI_PROPERTIES.cellMinimalWidth) {
                        return false;
                    }
                    widthNoCut = Math.min(UI_PROPERTIES.baseCellWidth, widthToNextSlot);
                } else {
                    widthNoCut = UI_PROPERTIES.baseCellWidth;
                }

                var widthToDraw = Math.max(0, widthNoCut - metrics.leftCut);
                var rect = new Konva.Rect({
                    x: metrics.absoluteX + metrics.leftCut, y: yOffset, width: widthToDraw, height: UI_PROPERTIES.cellHeight,
                    fill: slotState.getColor(), stroke: 'white', strokeWidth: 1
                });
                rect.slotState = slotState;
                slotState.rect = rect;
                var group = new Konva.Group();
                group.add(rect);

                if (slotState.status.abbrevation !== '' && widthToDraw > slotState.status.abbrevationWidth) {
                    var abbr = new Konva.Text({
                        x: metrics.absoluteX + metrics.leftCut,
                        y: yOffset + UI_PROPERTIES.workflowNameYOffset,
                        align: 'center',
                        width: widthToDraw,
                        text: slotState.status.abbrevation,
                        fontSize: UI_PROPERTIES.slotStateAbbreviationFontSize
                    });
                    abbr.slotState = slotState;
                    slotState.abbr = abbr;
                    group.add(abbr);
                }
                slotState.shape = group;
                shapes.push(group);
            }
        }
        return shapes;
    }

    function drawSlots(yOffset, slotStatesSeq) {
        var shapes = prepareSlots(yOffset, slotStatesSeq);
        if (shapes) {
            for (var i=0; i < shapes.length; i++) {
                slotStatesView.activeLayer.add(shapes[i]);
            }
            return true;
        } else {
            return false;
        }
    }

    function drawSolidSlot(y, width, workflow) {

        var rect = new Konva.Rect({
            x: mutableDims.cellDataOffset, y: y, width: width, height: UI_PROPERTIES.cellHeight,
            fill: UI_PROPERTIES.cellColorZoomMe, stroke: 'white', strokeWidth: 1,
            transformsEnabled: 'position'
        });
        //rect.object = workflow;
        //workflow.shape = rect;

        slotStatesView.activeLayer.add(rect);

        var dashing = new Konva.Shape({
            drawFunc: function(context) {
                context.beginPath();

                var stepY = UI_PROPERTIES.cellHeight -  UI_PROPERTIES.ZOOM_TOO_LOW_STROKE_MARGIN * 2;
                var stepX = UI_PROPERTIES.ZOOM_TOO_LOW_STROKE_STEP;

                var lineX = mutableDims.cellDataOffset + UI_PROPERTIES.ZOOM_TOO_LOW_STROKE_MARGIN;
                var lineY = y  + UI_PROPERTIES.ZOOM_TOO_LOW_STROKE_MARGIN;// + UI_PROPERTIES.cellHeight - UI_PROPERTIES.ZOOM_TOO_LOW_STROKE_MARGIN;

                var lineXTo = lineX - stepX * 2;
                while (lineXTo < mutableDims.cellDataOffset + width) {
                    context.moveTo(lineX, lineY);
                    context.lineTo(lineXTo, lineY + stepY);
                    lineX += stepX;
                    lineXTo = lineX - stepX * 2;
                }
                context.closePath();
                context.fillStrokeShape(this);
            },
            stroke: 'white',
            strokeWidth: 2
        });
        slotStatesView.activeLayer.add(dashing);

        var textWidth = getTextWidth(UI_PROPERTIES.zoomInDesrcText, UI_PROPERTIES.slotStateAbbreviationFontSize);

        var descrBackground = new Konva.Rect({
            x: mutableDims.cellDataOffset + (width - textWidth)/2,
            y: y + UI_PROPERTIES.workflowNameYOffset,
            width: textWidth, height: UI_PROPERTIES.cellHeight - UI_PROPERTIES.workflowNameYOffset * 2,
            fill: UI_PROPERTIES.cellColorZoomMe, transformsEnabled: 'position'
        });

        slotStatesView.activeLayer.add(descrBackground);

        var descr = new Konva.Text({
            x: mutableDims.cellDataOffset + (width - textWidth)/2,
            y: y + UI_PROPERTIES.workflowNameYOffset,
            width: textWidth,
            height: UI_PROPERTIES.cellHeight - UI_PROPERTIES.workflowNameYOffset * 2,
            align: 'center', text: UI_PROPERTIES.zoomInDesrcText,
            fontSize: UI_PROPERTIES.slotStateAbbreviationFontSize
        });

        slotStatesView.activeLayer.add(descr);
    }


}

function RrmanServerClient() {

    this.getConfig = function(onGetConfig) {
        $.ajax({
            dataType: "json",
            url: "rrman-config",
            success: onGetConfig
        });
    };

    this.rerunSlot = function(slot) {
        $.ajax({
            type: "POST",
            url: "rerun",
            data: { time: DateUtils.toCelosUTCString(slot.date), id: slot.workflow.id },
            success: function() {
                $.ajax({
                    dataType: "json",
                    data: { time: DateUtils.toCelosUTCString(slot.date), id: slot.workflow.id },
                    url: "slot-state",
                    success: function(data) {
                        slot.status = STATUSES[data.status];
                        slot.externalID = data.externalID;
                        slot.repaint();
                    }
                })
            }
        });
    };

    this.update = function(date) {
        this.getConfig(function(config) {
            var forDate = DateUtils.addMs(date, 3 * DAY_MS);
            $.ajax({
                dataType: "json",
                data: { time: DateUtils.toCelosUTCString(forDate) },
                url: "workflow-slots",
                success: function(workflowsData) {
                    onWorkflowsData(workflowsData, config);
                }
            })
        });
    };

    function onWorkflowsData(workflowsData, rrmanConfig) {

        var wgContent = [];
        for (var i = 0; i < workflowsData.workflows.length; i++) {
            var wfData = workflowsData.workflows[i];

            var workflow = new Workflow(wfData.id, wfData.info);

            var slotStatesArr = [];
            for (var j=0; j < wfData.slots.length; j++) {
                var slot = wfData.slots[j];
                var date = new Date(slot.time);
                var slotState = new SlotState(STATUSES[slot.status], workflow, date, slot.externalID, slot.retryCount);
                slotStatesArr.push([date, slotState]);
            }
            var slotStates = Immutable.OrderedMap(slotStatesArr);
            wgContent.push([workflow, slotStates]);
        }
        var model = new RrmanModel(rrmanConfig, new Immutable.Map(wgContent));
        $(document).trigger("rrman:model_updated", model);
    }
}
