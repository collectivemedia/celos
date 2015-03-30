const SECOND_MS = 1000;
const MINUTE_MS = 60 * SECOND_MS;
const HOUR_MS = 60 * MINUTE_MS;
const DAY_MS = 24 * HOUR_MS;
const SAMPLE_DATE = '2015-02-05 13:23 UTC';
const SUCCESS = { name: 'SUCCESS' };
const FAILED = { name: 'FAILED' };
const UI_PROPERTIES = {
    //const ZOOM_TOO_LOW_STROKE_MARGIN = 10;
    //const ZOOM_TOO_LOW_STROKE_STEP = 5;

    workflowNameMargin: 20,
    workflowNameYOffset: 8,
    workflowGroupNameFontSize: 22,
    workflowNameFontSize: 20,
    dateTextFontSize: 17,
    baseCellWidth: 69,
    cellHeight: 36,
    scrollbarWidth: 20,
    cellMinimalWidth: 3,
    cellColorSuccess: '#caffca',
    cellColorFailed: '#ffcaca',
    cellColorSelected :'#99ff99'
};

function SlotState(status, workflow, date, duration, externalId, retryCount) {
    this.status = status;
    this.workflow = workflow;
    this.date = date;
    this.expectedDuration = duration;
    this.externalId = externalId;
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
        return UI_PROPERTIES.cellColorSelected;
    } else {
        if (this.status == FAILED) {
            return UI_PROPERTIES.cellColorFailed;
        } else {
            return UI_PROPERTIES.cellColorSuccess;
        }
    }
};

function WorkflowGroup(name, workflowToSlotsMap) {
    this.name = name;
    this.workflowToSlotsMap = workflowToSlotsMap;
}

function Workflow(id, author) {
    this.id = id;
    this.author = author;
}

Workflow.prototype.getDescription = function() {
    return 'Workflow ' + this.id + ': [author: ' + this.author + ']';
};


function RrmanModel(updateTime, workflowGroups) {
    this.updateTime = updateTime;
    this.workflowGroups = workflowGroups;
}


var DateUtils = (function(){

    var _this = this;

    function pad(n){
        return n < 10 ? '0' + n : n
    }

    this.distractMs = function(date, ms) {
        return new Date(date.getTime() - ms);
    };

    this.getPaddedUTCHours = function(date){
        return pad(date.getUTCHours());
    };

    this.getPaddedUTCMinutes = function(date){
        return pad(date.getUTCMinutes());
    };

    this.toCelosUTCString = function(date) {
        return date.toISOString().slice(0, 10) + " " + _this.getPaddedUTCHours(date) + ':' + _this.getPaddedUTCMinutes(date) + ":" + pad(date.getSeconds());
    };

    return this;

})();

var SelectManager = function() {
    var selectedSlots = new Immutable.Set();

    this.addSlot = function(slot) {
        selectedSlots = selectedSlots.add(slot.getSlotID());
    };

    this.removeSlot = function(slot) {
        selectedSlots = selectedSlots.remove(slot.getSlotID());
    };

    this.containsSlot = function(slot) {
        return selectedSlots.contains(slot.getSlotID());
    }
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
        pagingOffsetDate = zoomers[currentZoomerIndex].getDateAfterSteps(_this.getPagingOffsetDate(), cellNum - 1);
        view.repaint();

        setTimeout(function() {
            $(document).trigger("rrman:request_data", pagingOffsetDate);
        }, 50);
    };

    this.prevPage = function () {
        var cellNum = view.getCellsNumber();
        var newDate = zoomers[currentZoomerIndex].getDateAfterSteps(_this.getPagingOffsetDate(), -(cellNum - 1));
        if (new Date().getTime() - newDate.getTime() < zoomers[currentZoomerIndex].baseCellDuration) {
            newDate = null;
        }
        pagingOffsetDate = newDate;
        view.repaint();

        setTimeout(function() {
            $(document).trigger("rrman:request_data", pagingOffsetDate);
        }, 50);
    };

};

function init() {

    var slotStatesPanel = $("#slotStatesPanel");
    var slotStatesStage = new Konva.Stage({
        container: 'slotStatesPanel',
        width: slotStatesPanel.width(),
        height: slotStatesPanel.height()
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

    var slotStatesContext = { activeLayer: slotStatesLayerActive, inactiveLayer: slotStatesLayerInactive, stage: slotStatesStage, panel: slotStatesPanel };
    var dateContext = { layer: dateLayer, stage: dateStage, panel: datePanel };

    var rrmanView = new RrmanView(slotStatesContext, dateContext);
    var client = new RrmanServerClient(rrmanView);

    $(document).on( "rrman:model_updated", function(evt, model) {
        rrmanView.setModel(model);
        rrmanView.repaint();
    });
    $(document).on( "rrman:request_data", function(evt, date) {
        client.update(date);
    });
    rrmanView.setupEventListeners();
    rrmanView.triggerUpdate();

    setInterval(function () {
        rrmanView.triggerUpdate();
    }, 5000);

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

function RrmanView(slotStatesView, datesView) {

    var _this = this;
    var mutableDims;
    var rrmanModel;

    var selection = new SelectManager();
    var zoomer = new ZoomManager(this);
    var initialCanvasSizeUpdate = false;
    
    this.repaint = function() {
        if (!initialCanvasSizeUpdate) {
            _this.updateCanvasSize();
            initialCanvasSizeUpdate = true;
        }
        updateMutableDimensions();
        repaintSlotStatePanel();
        repaintDatePanel();
    };

    this.triggerUpdate = function() {
        $(document).trigger("rrman:request_data", _this.getCurrentDate());
    };

    this.setupEventListeners = function() {
        slotStatesView.stage.on('mouseover mousemove dragmove', function (evt) {
            if (evt.target && evt.target.object) {
                showCellDetails(evt.target.object);
            } else {
                clearCellDetails();
            }
        });
        slotStatesView.stage.on('click', function (evt) {
            if (evt.target && evt.target.object) {
                var obj = evt.target.object;
                obj.selected = !obj.selected;
                if (obj.selected) {
                    selection.addSlot(obj);
                    evt.target.setFill(obj.getColor());
                } else {
                    selection.removeSlot(obj);
                }
                evt.target.setFill(obj.getColor());
                evt.target.draw();
            }
        });
        slotStatesView.stage.on('mouseout', function () {
            clearCellDetails();
        });
        document.getElementById("zoomIn").addEventListener("click", zoomer.zoomIn);
        document.getElementById("zoomOut").addEventListener("click", zoomer.zoomOut);
        document.getElementById("nextPage").addEventListener("click", zoomer.nextPage);
        document.getElementById("prevPage").addEventListener("click", zoomer.prevPage);
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
        function reducer(prevValue, workflowGroup) {
            return prevValue + workflowGroup.workflowToSlotsMap.count() * UI_PROPERTIES.cellHeight + UI_PROPERTIES.cellHeight * 2;
        }
        var totalHeight = rrmanModel.workflowGroups.reduce(reducer, 0) - UI_PROPERTIES.cellHeight;
        slotStatesView.stage.setWidth(slotStatesView.panel.width() - UI_PROPERTIES.scrollbarWidth);
        slotStatesView.stage.setHeight(totalHeight);
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

    function resetSelectedMarkers(_model) {
        function toSlots(datesToSlotStates) {
            return datesToSlotStates.values();
        }
        function toSlotsMap(workflowGroup) {
            return workflowGroup.workflowToSlotsMap.values();
        }
        function updateSlotState(slotState) {
            if (selection.containsSlot(slotState)) {
                slotState.selected = true;
            }
        }

        _model.workflowGroups.flatMap(toSlotsMap).flatMap(toSlots).forEach(updateSlotState);
    }

    function updateMutableDimensions() {
        var dateWidth = getTextWidth(SAMPLE_DATE, UI_PROPERTIES.dateTextFontSize);

        function getMaxWorkflowNameWidth(wfName1, wfName2) {
            var width1 = getTextWidth(wfName1, UI_PROPERTIES.workflowNameFontSize);
            var width2 = getTextWidth(wfName2, UI_PROPERTIES.workflowNameFontSize);
            return Math.max(width1, width2);
        }

        function toWorkflowNames(workflowGroup) {
            return workflowGroup.workflowToSlotsMap.keys();
        }

        var workflowNameWidth = rrmanModel.workflowGroups.flatMap(toWorkflowNames).reduce(getMaxWorkflowNameWidth)
        var maxWidth = Math.max(dateWidth, workflowNameWidth);

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

        var aDate = new Date(zoomer.getPagingOffsetDate().getTime());
        var x = mutableDims.cellDataOffset;
        while (x + UI_PROPERTIES.baseCellWidth < datesView.stage.width()) {
            if (aDate.getUTCHours() == 0) {

            }
            var aText = new Konva.Text({
                x: x,
                y: 0,
                textColor: 'red',
                backgroundColor: 'green',
                width: UI_PROPERTIES.baseCellWidth,
                align: 'center',
                text: zoomer.getCurrentZoom().getDescription(aDate),
                fontSize: UI_PROPERTIES.dateTextFontSize, fontStyle: 'bold'
            });
            datesView.layer.add(aText);
            aDate = zoomer.getCurrentZoom().getDateAfterSteps(aDate, 1);
            x += UI_PROPERTIES.baseCellWidth;
        }

        datesView.layer.batchDraw();
    }

    function repaintSlotStatePanel() {

        var oldLayer = slotStatesView.activeLayer;
        slotStatesView.activeLayer = slotStatesView.inactiveLayer;
        slotStatesView.inactiveLayer = oldLayer;

        var yOffset = 0;

        function drawWorkflowGroup(wfGroup) {
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

            wfGroup.workflowToSlotsMap.forEach(function(slotStatesMap, workflow) {
                drawWorkflow(yOffset, workflow, slotStatesMap);
                yOffset += UI_PROPERTIES.cellHeight;
            });
            yOffset += UI_PROPERTIES.cellHeight;
        }

        rrmanModel.workflowGroups.forEach(drawWorkflowGroup);

        slotStatesView.activeLayer.listening(true);
        slotStatesView.activeLayer.setVisible(true);
        slotStatesView.activeLayer.batchDraw();
        slotStatesView.inactiveLayer.listening(false);
        slotStatesView.inactiveLayer.setVisible(false);
        slotStatesView.inactiveLayer.destroyChildren();
    }

    function isSlotStateCellBigEnough(slotStatesMap) {
        var iterator = slotStatesMap.values();
        var next = iterator.next();
        var cellSizeFactor = next.value.expectedDuration / zoomer.getCurrentZoom().baseCellDuration;
        var width = cellSizeFactor * UI_PROPERTIES.baseCellWidth;
        return width >= UI_PROPERTIES.cellMinimalWidth;
    }

    function drawWorkflow(yOffset, workflow, slotStatesMap) {

        var workflowName = new Konva.Text({
            x: UI_PROPERTIES.workflowNameMargin, y: yOffset + UI_PROPERTIES.workflowNameYOffset,
            align: 'right', text: workflow.id, width: mutableDims.workflowNameWidth,
            fontSize: UI_PROPERTIES.workflowNameFontSize
        });

        slotStatesView.activeLayer.add(workflowName);

        if (isSlotStateCellBigEnough(slotStatesMap)) {
            drawSlots(yOffset, slotStatesMap);
        } else {
            drawSolidSlot(yOffset, slotStatesView.stage.width(), workflow);
        }
    }

    function drawSlots(yOffset, slotStatesMap) {

        function beforeNow(x, y) {
            return y.getTime() <= _this.getCurrentDate().getTime();
        }

        function drawSlot(slotState) {
            var millisInPixel = zoomer.getCurrentZoom().baseCellDuration / UI_PROPERTIES.baseCellWidth;
            var x = mutableDims.cellDataOffset + (_this.getCurrentDate().getTime() - slotState.date.getTime()) / millisInPixel;
            if (x <= slotStatesView.stage.getWidth()) {
                var cellSizeFactor = Math.min(1, slotState.expectedDuration / zoomer.getCurrentZoom().baseCellDuration);
                var widthToDraw = cellSizeFactor * UI_PROPERTIES.baseCellWidth;

                var rect = new Konva.Rect({
                    x: x, y: yOffset, width: widthToDraw, height: UI_PROPERTIES.cellHeight,
                    fill: slotState.getColor(), stroke: 'white', strokeWidth: 1
                });

                rect.object = slotState;
                slotStatesView.activeLayer.add(rect);
            }
        }

        slotStatesMap.filter(beforeNow).forEach(drawSlot);
    }

    function drawSolidSlot(y, width, workflow) {

        var rect = new Konva.Rect({
            x: mutableDims.cellDataOffset, y: y, width: width, height: UI_PROPERTIES.cellHeight,
            fill: UI_PROPERTIES.cellColorSuccess, stroke: 'white', strokeWidth: 1,
            transformsEnabled: 'position'
        });
        rect.object = workflow;
        workflow.shape = rect;

        slotStatesView.activeLayer.add(rect);

        //var stepY = CELL_HEIGHT - ZOOM_TOO_LOW_STROKE_MARGIN * 2;
        //var stepX = ZOOM_TOO_LOW_STROKE_STEP * 2;
        //var lineX = -ZOOM_TOO_LOW_STROKE_MARGIN;
        //var lineY = y + CELL_HEIGHT - ZOOM_TOO_LOW_STROKE_MARGIN;
        //
        //var coords = [];
        //while (lineX + stepX < width) {
        //    coords.push({ x: lineX, y: lineY})
        //    coords.push({ x: lineX + stepX, y: lineY + stepY})
        //    lineX += stepX * 2;
        //}
        //var poly = new fabric.Polyline(coords, {
        //    stroke: 'white',
        //    fill: CELL_COLOR,
        //    strokeWidth: 2,
        //    left: x,
        //    top: y + ZOOM_TOO_LOW_STROKE_MARGIN
        //});
        //
        //console.add(poly);
    }


}

function RrmanServerClient() {

    var slotLengths = [5 * MINUTE_MS, 30 * MINUTE_MS, HOUR_MS, HOUR_MS];

    this.update = function(date) {
        var model = getDataFor(DateUtils.distractMs(date, -3 * DAY_MS));
        $(document).trigger("rrman:model_updated", model);
    };

    function getDataFor(forDate) {
        var wfList = getWorkflowList();
        var wgContent = [];
        if (forDate.getTime() > new Date().getTime) {
            forDate = new Date();
        }
        for (var i=0; i < wfList.length; i++) {
            var slotStates = getWorkflowSlots(wfList[i], forDate, i);
            wgContent.push([wfList[i], slotStates]);
        }
        var workflowGroup = new WorkflowGroup("Default Group", new Immutable.OrderedMap(wgContent));
        return new RrmanModel(forDate, new Immutable.Seq([ workflowGroup ]));
    }

    function getWorkflowList() {
        var wfs = [];
        for(var i = 0; i < 20; i++) {
            wfs.push(new Workflow("workflow-" + i, "John Doe"));
        }
        return wfs;
    }

    function getWorkflowSlots(workflow, time, fakeNum) {
        var slotStatesMap = [];
        var duration = slotLengths[fakeNum % slotLengths.length];
        time = DateUtils.distractMs(time, time.getTime() % duration);
        var endTime = DateUtils.distractMs(time, 7 * DAY_MS);
        while (time.getTime() > endTime.getTime() ) {
            if (Math.random() > 0.02) {
                var status = Math.random() > 0.01 ? SUCCESS : FAILED;
                var slotState = new SlotState(status, workflow, time, duration, "externalId@" + workflow.id + "@" + DateUtils.toCelosUTCString(time));
                slotStatesMap.push([time, slotState]);
            }
            time = DateUtils.distractMs(time, duration);
        }
        return new Immutable.OrderedMap(slotStatesMap);
    }
}
