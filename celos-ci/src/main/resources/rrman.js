const SECONDS_MS = 1000;
const MINUTE_MS = 60 * SECONDS_MS;
const HOUR_MS = 60 * MINUTE_MS;

const SUCCESS = { name: 'SUCCESS' };
const FAILED = { name: 'FAILED' };

const CONSTANTS = {
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

function ZoomView(baseCellDuration, descFunc) {
    this.baseCellDuration = baseCellDuration;
    this.getDescription = descFunc;
}

ZoomView.prototype.getDateAfterSteps = function(date, steps) {
    var newDate = new Date(date);
    newDate.setUTCMinutes(newDate.getUTCMinutes() - (this.baseCellDuration / MINUTE_MS) * steps);
    return newDate;
};

var Zoomer = function(view) {

    var zoomers = [ new ZoomView(2 * HOUR_MS, function(date) { return date.getPaddedUTCHours() + ":00"; }),
                    new ZoomView(HOUR_MS, function(date) { return date.getPaddedUTCHours() + ":00"; }),
                    new ZoomView(30 * MINUTE_MS, function(date) { return date.getPaddedUTCHours() + ":" + date.getPaddedUTCMinutes(); }),
                    new ZoomView(15 * MINUTE_MS, function(date) { return date.getPaddedUTCHours() + ":" + date.getPaddedUTCMinutes(); }),
                    new ZoomView(5 * MINUTE_MS, function(date) { return date.getPaddedUTCHours() + ":" + date.getPaddedUTCMinutes(); })];

    var currentZoom = 1;
    var pagingOffsetDate = null;
    this.getPagingOffsetDate = getPagingOffsetDate;

    this.getCurrentZoom = function() {
        return zoomers[currentZoom];
    };

    function getPagingOffsetDate() {
        if (pagingOffsetDate) {
            return pagingOffsetDate;
        }
        return new Date();
    };

    this.zoomIn = function () {
        if (currentZoom < zoomers.length - 1) {
            currentZoom++;
        }
        view.repaint();
    };

    this.zoomOut = function () {
        if (currentZoom > 0) {
            currentZoom--;
        }
        view.repaint();
    };

    this.nextPage = function() {
        var cellNum = view.getCellsNumber();
        pagingOffsetDate = zoomers[currentZoom].getDateAfterSteps(getPagingOffsetDate(), cellNum - 1);

        $(document).trigger("rrman:request_data");
        view.repaint();
    };

    this.prevPage = function () {
        var cellNum = view.getCellsNumber();
        var newDate = zoomers[currentZoom].getDateAfterSteps(getPagingOffsetDate(), -(cellNum - 1));
        if (new Date().getTime() - newDate.getTime() < zoomers[currentZoom].baseCellDuration) {
            newDate = null;
        }
        pagingOffsetDate = newDate;
        $(document).trigger("rrman:request_data");
        view.repaint();
    };


};

(function() {
    function pad(n){
        return n < 10 ? '0'+n : n
    }

    Date.prototype.getPaddedUTCHours = function(){
        return pad(this.getUTCHours());
    }

    Date.prototype.getPaddedUTCMinutes = function(){
        return pad(this.getUTCMinutes());
    }

    Date.prototype.toCelosUTCString = function() {
        return this.toISOString().slice(0, 10) + " " + this.getPaddedUTCHours() + ':' + this.getPaddedUTCMinutes() + ":" + pad(this.getSeconds());
    };
})();


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
    client.update();
    rrmanView.updateCanvasSize();
    rrmanView.repaint();


    $(document).on( "rrman:model_updated", function() {
        rrmanView.repaint();
    });
    $(document).on( "rrman:request_data", function() {
        client.update();
    });

    setInterval(function () {
        $(document).trigger("rrman:request_data");
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

    var mutableDims;
    var model;

    var zoomer = new Zoomer(this);

    this.repaint = repaint;
    this.updateCanvasSize = updateCanvasSize;
    setupEventListeners();

    this.getCurrentDate = function() {
        return zoomer.getPagingOffsetDate();
    }

    this.setModel = function(_model) {
        model = _model;
    }

    this.getCellsNumber = function () {
        var datesPanelWidth = datesView.stage.width() - mutableDims.cellDataOffset;
        return Math.floor(datesPanelWidth / CONSTANTS.baseCellWidth);
    };

    function updateMutableDims() {
        var workflowNameWidth = getTextWidth('2015-02-05 13:23 UTC', 17);
        for (var i=0; i < model.workflowGroups.length; i++) {
            var entries = model.workflowGroups[i].workflowToSlotsMap.keys();
            var next = entries.next();
            while (!next.done) {
                var width = getTextWidth(next.value.id, CONSTANTS.workflowNameFontSize);
                workflowNameWidth = Math.max(workflowNameWidth, width);
                next = entries.next();
            }
        }
        mutableDims = {
            workflowNameWidth: workflowNameWidth,
            cellDataOffset: workflowNameWidth + 2 * CONSTANTS.workflowNameMargin
        }
    }

    function repaint() {
        updateMutableDims();
        var callback1 = repaintDatePanel();
        var callback2 = repaintSlotStatePanel();
        setTimeout(function() {
            callback2();
            callback1();
        }, 1);
    }

    function repaintDatePanel() {
        datesView.layer.destroyChildren();
        var dateText = new Konva.Text({
            x: CONSTANTS.workflowNameMargin,
            y: 0,
            align: 'right',
            text: zoomer.getPagingOffsetDate().toCelosUTCString(),
            fontSize: CONSTANTS.dateTextFontSize, fontStyle: 'bold'
        });
        datesView.layer.add(dateText);

        var aDate = new Date(zoomer.getPagingOffsetDate().getTime());
        var x = mutableDims.cellDataOffset;
        while (x + CONSTANTS.baseCellWidth < datesView.stage.width()) {
            var aText = new Konva.Text({
                x: x,
                y: 0,
                width: CONSTANTS.baseCellWidth,
                align: 'center',
                text: zoomer.getCurrentZoom().getDescription(aDate),
                fontSize: CONSTANTS.dateTextFontSize, fontStyle: 'bold'
            });
            datesView.layer.add(aText);
            aDate = zoomer.getCurrentZoom().getDateAfterSteps(aDate, 1);
            x += CONSTANTS.baseCellWidth;
        }

        return function() {
            datesView.layer.batchDraw();
        }
    }

    function repaintSlotStatePanel() {

        var oldLayer = slotStatesView.activeLayer;
        slotStatesView.activeLayer = slotStatesView.inactiveLayer;
        slotStatesView.inactiveLayer = oldLayer;

        var yOffset = 0;
        for (var i=0; i < model.workflowGroups.length; i++) {
            var workflowGroupName = new Konva.Text({
                x: CONSTANTS.workflowNameMargin,
                y: yOffset + CONSTANTS.workflowNameYOffset,
                align: 'right',
                text: model.workflowGroups[i].name,
                width: mutableDims.workflowNameWidth,
                fontSize: CONSTANTS.workflowGroupNameFontSize,
                fontStyle: 'bold'
            });
            yOffset += CONSTANTS.cellHeight;
            slotStatesView.activeLayer.add(workflowGroupName);

            var entries = model.workflowGroups[i].workflowToSlotsMap.entries();
            var next = entries.next();
            while (!next.done) {
                drawWorkflow(yOffset, next.value[0], next.value[1]);
                yOffset += CONSTANTS.cellHeight;
                next = entries.next();
            }
            yOffset += CONSTANTS.cellHeight;
        }

        var callback = function () {
            slotStatesView.activeLayer.listening(true);
            slotStatesView.activeLayer.setVisible(true);
            slotStatesView.activeLayer.batchDraw();
            slotStatesView.inactiveLayer.listening(false);
            slotStatesView.inactiveLayer.setVisible(false);
            slotStatesView.inactiveLayer.destroyChildren();
        };
        return callback;
    }

    function updateCanvasSize() {
        var totalHeight = 0;
        for (var i=0; i < model.workflowGroups.length; i++) {
            totalHeight += model.workflowGroups[i].workflowToSlotsMap.count() * CONSTANTS.cellHeight;
            totalHeight += CONSTANTS.cellHeight * 2;
        }
        totalHeight -= CONSTANTS.cellHeight;

        slotStatesView.stage.setWidth(slotStatesView.panel.width() - CONSTANTS.scrollbarWidth);
        slotStatesView.stage.setHeight(totalHeight);
        slotStatesView.stage.batchDraw();;
    }

    function isSlotStateCellBigEnough(slotStates) {
        var width = 0;
        for (var i = 0; i < slotStates.length; i++) {
            var cellSizeFactor = slotStates[i].expectedDuration / zoomer.getCurrentZoom().baseCellDuration;
            width += cellSizeFactor * CONSTANTS.baseCellWidth;
        }
        return width / slotStates.length >= CONSTANTS.cellMinimalWidth;
    }

    function drawWorkflow(yOffset, workflow, slotStates) {

        var workflowName = new Konva.Text({
            x: CONSTANTS.workflowNameMargin, y: yOffset + CONSTANTS.workflowNameYOffset,
            align: 'right', text: workflow.id, width: mutableDims.workflowNameWidth,
            fontSize: CONSTANTS.workflowNameFontSize
        });

        slotStatesView.activeLayer.add(workflowName);

        if (isSlotStateCellBigEnough(slotStates)) {
            drawSlotsStates(yOffset, slotStates);
        } else {
            drawWorkflowSlot(yOffset, slotStatesView.stage.width(), workflow);
        }
    }

    function drawSlotsStates(yOffset, slotStates) {
        var x = mutableDims.cellDataOffset;
        var slotStatesSeq = new Immutable.Seq(slotStates);
        var iterator = slotStatesSeq.filter( function(x) { return x.date.getTime() <= zoomer.getPagingOffsetDate().getTime() }).values();
        var next = iterator.next();
        while (!next.done) {
            var slotState = next.value;
            if (x <= slotStatesView.stage.getWidth()) {
                var cellSizeFactor = slotState.expectedDuration / zoomer.getCurrentZoom().baseCellDuration;
                var width = cellSizeFactor * CONSTANTS.baseCellWidth;

                var color = slotState.status === FAILED ? CONSTANTS.cellColorFailed : CONSTANTS.cellColorSuccess;

                var rect = new Konva.Rect({
                    x: x, y: yOffset, width: width, height: CONSTANTS.cellHeight,
                    fill: color, stroke: 'white', strokeWidth: 1
                });

                rect.object = slotState;
                slotStatesView.activeLayer.add(rect);
            }
            x += width;
            next = iterator.next();
        }
    }

    function drawWorkflowSlot(y, width, workflow) {

        var rect = new Konva.Rect({
            x: mutableDims.cellDataOffset, y: y, width: width, height: CONSTANTS.cellHeight,
            fill: CONSTANTS.cellColorSuccess, stroke: 'white', strokeWidth: 1,
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

    function setupEventListeners() {
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
                    evt.target.setFill(CONSTANTS.cellColorSelected);
                } else {
                    evt.target.setFill(CONSTANTS.cellColorSuccess);
                }
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
        window.addEventListener('resize', updateCanvasSize, false);
    }

}

function SlotState(status, workflow, date, duration) {
    this.status = status;
    this.workflow = workflow;
    this.date = date;
    this.expectedDuration = duration;
}

SlotState.prototype.getDescription = function() {
    return 'Slot ' + this.workflow.id + '@' + this.date.toISOString() + ': ' + this.status.name;
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


function RrmanServerClient(view) {

    var slotLengths = [5 * MINUTE_MS, 30 * MINUTE_MS, HOUR_MS, HOUR_MS];

    function distractMs(time, ms) {
        return new Date(time.getTime() - ms);
    }

    this.update = function() {
        view.setModel(getDataFor(distractMs(view.getCurrentDate(), -24 * 3 * HOUR_MS)))
        $(document).trigger("rrman:model_updated");
    }

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
        return new RrmanModel(forDate, [ workflowGroup ])
    };

    function getWorkflowList() {
        var wfs = [];
        for(var i = 0; i < 20; i++) {
            wfs.push(new Workflow("workflow-" + i, "John Doe"));
        }
        return wfs;
    };

    function getWorkflowSlots(workflow, forDate, fakeNum) {
        var slotStates = [];
        var time = forDate;
        var endTime = distractMs(time, 7*24*HOUR_MS);
        while (time.getTime() > endTime.getTime() ) {
            var duration = slotLengths[fakeNum % slotLengths.length];
            var status = Math.random() > 0.1 ? SUCCESS : FAILED;
            slotStates.push(new SlotState(status, workflow, time, duration));
            time = distractMs(time, duration);
        }
        return slotStates;
    }
}



function RrmanModel(updateTime, workflowGroups) {
    this.updateTime = updateTime;
    this.workflowGroups = workflowGroups;
}