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

var twoHoursZoomer = {
    baseCellDuration: 2 * HOUR_MS,
    getDesc: function(date) {
        return date.getPaddedUTCHours();
    },
    updateDateToNextStep: function(date) {
        date.setUTCHours(date.getUTCHours() - 2);
        return date;
    }
};

var hourZoomer = {
    baseCellDuration: HOUR_MS,
    getDesc: function(date) {
        return date.getPaddedUTCHours();
    },
    updateDateToNextStep: function(date) {
        date.setUTCHours(date.getUTCHours() - 1);
        return date;
    }
};

var halfHourZoomer = {
    baseCellDuration: 30 * MINUTE_MS,
    getDesc: function(date) {
        return date.getPaddedUTCHours() + ":" + date.getPaddedUTCMinutes();
    },
    updateDateToNextStep: function(date) {
        date.setUTCMinutes(date.getUTCMinutes() - 30);
        return date;
    }
};

var fifteenMinZoomer = {
    baseCellDuration: 15 * MINUTE_MS,
    getDesc: function(date) {
        return date.getPaddedUTCHours() + ":" + date.getPaddedUTCMinutes();
    },
    updateDateToNextStep: function(date) {
        date.setUTCMinutes(date.getUTCMinutes() - 15);
        return date;
    }
};

var fiveMinZoomer = {
    baseCellDuration: 5 * MINUTE_MS,
    getDesc: function(date) {
        return date.getPaddedUTCHours() + ":" + date.getPaddedUTCMinutes();
    },
    updateDateToNextStep: function(date) {
        date.setUTCMinutes(date.getUTCMinutes() - 5);
        return date;
    }
};
var currentZoom = 3;
var zoomers = [fiveMinZoomer, fifteenMinZoomer, halfHourZoomer, hourZoomer, twoHoursZoomer];


function SlotState(status, workflow, date, duration) {
    this.status = status;
    this.workflow = workflow;
    this.date = date;
    this.expectedDuration = duration;
}

(function() {
    function pad(n){
        return n<10 ? '0'+n : n
    }

    Date.prototype.getPaddedUTCHours = function(){
        return pad(this.getUTCHours());
    }

    Date.prototype.getPaddedUTCMinutes = function(){
        return pad(this.getUTCMinutes());
    }

    Date.prototype.toCelosUTCString = function() {
        return this.toISOString().slice(0, 10) + " " + this.getPaddedUTCHours() + ':' + this.getPaddedUTCMinutes() + " UTC";
    };
})();

SlotState.prototype.getDescription = function() {
    return 'Slot ' + this.workflow.id + '@' + this.date.toISOString() + ': ' + this.status.name;
};

function WorkflowGroup(name, workflowToSlotsMap) {
    this.name = name;
    this.workflowToSlotsMap = workflowToSlotsMap;
}

function Workflow(id, description, author) {
    this.id = id;
    this.description = description;
    this.author = author;
}

Workflow.prototype.getDescription = function() {
    return 'Workflow ' + this.id + ': [author: ' + this.author + ']';
};

function init() {

    var slotStatesPanel = $("#slotStatesPanel");
    var slotStatesStage = new Konva.Stage({
        container: 'slotStatesPanel',
        width: slotStatesPanel.width(),
        height: slotStatesPanel.height()
    });
    var slotStatesLayer = new Konva.Layer();
    slotStatesStage.add(slotStatesLayer);

    var datePanel = $("#datePanel");
    var dateStage = new Konva.Stage({
        container: 'datePanel',
        width: datePanel.width(),
        height: datePanel.height()
    });
    var dateLayer = new Konva.Layer();
    dateStage.add(dateLayer);

    var slotStatesContext = { layer: slotStatesLayer, stage: slotStatesStage, panel: slotStatesPanel };
    var dateContext = { layer: dateLayer, stage: dateStage, panel: datePanel };

    var rrmanView = new RrmanView(slotStatesContext, dateContext);
    rrmanView.setModel(new RrmanModel());

    document.getElementById("zoomIn").addEventListener("click", rrmanView.zoomIn);
    document.getElementById("zoomOut").addEventListener("click", rrmanView.zoomOut);
    document.getElementById("nextPage").addEventListener("click", rrmanView.nextPage);
    document.getElementById("prevPage").addEventListener("click", rrmanView.prevPage);

    window.addEventListener('resize', rrmanView.updateCanvasSize, false);

    rrmanView.updateCanvasSize();
    rrmanView.repaint();
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

    var rrmanModel;
    var pagingData;
    var mutableDims;

    this.updateCanvasSize = updateCanvasSize();
    this.repaint = repaint;

    slotStatesView.stage.on('mouseover mousemove dragmove', function(evt) {
        if (evt.target && evt.target.object) {
            showCellDetails(evt.target.object);
        } else {
            clearCellDetails();
        }
    });
    slotStatesView.stage.on('click', function(evt) {
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
    slotStatesView.stage.on('mouseout', function(evt) {
        clearCellDetails();
    });

    function getMutableDims() {
        var workflowNameWidth = getTextWidth('2015-02-05 13:23 UTC', 17);
        for (var i=0; i < rrmanModel.workflowGroups.length; i++) {
            var entries = rrmanModel.workflowGroups[i].workflowToSlotsMap.keys();
            var next = entries.next();
            while (!next.done) {
                var width = getTextWidth(next.value.id, CONSTANTS.workflowNameFontSize);
                workflowNameWidth = Math.max(workflowNameWidth, width);
                next = entries.next();
            }
        }
        return {
            workflowNameWidth: workflowNameWidth,
            cellDataOffset: workflowNameWidth + 2 * CONSTANTS.workflowNameMargin
        }
    }

    function repaint() {
        //var a1 = new Date().getTime();

        mutableDims = getMutableDims();
        if (!pagingData) {
            pagingData = { dateXOffset: 0, date: rrmanModel.startTime }
        }
        repaintDatePanel(mutableDims, pagingData, zoomers[currentZoom]);
        repaintSlotStatePanel(mutableDims);
        //var a2 = new Date().getTime();
        //console.log("Paint: " + (a2 - a1));

    };

    this.setModel = function(_rrmanModel) {
        rrmanModel = _rrmanModel;
    };

    this.zoomIn = function () {
        if (currentZoom > 0) {
            currentZoom--;
        }
        repaint();
    };

    this.zoomOut = function () {
        if (currentZoom < zoomers.length - 1) {
            currentZoom++;
        }
        repaint();
    };

    function calcDrawCellNum() {
        var datesPanelWidth = datesView.stage.width() - mutableDims.cellDataOffset;
        var cellNum = Math.max(0, Math.floor(datesPanelWidth / CONSTANTS.baseCellWidth) - 1);
        return cellNum;
    }

    this.nextPage = function() {
        var cellNum = calcDrawCellNum();
        var thisDate = new Date(pagingData.date.getTime());
        thisDate.setUTCHours(thisDate.getUTCHours() - cellNum);
        pagingData = { dateXOffset: 0, date: thisDate }

        repaint();
    };

    this.prevPage = function () {

        var cellNum = calcDrawCellNum();

        var thisDate = new Date(pagingData.date.getTime());
        thisDate.setUTCHours(thisDate.getUTCHours() + cellNum);
        if (thisDate.getTime() > new Date().getTime()) {
            thisDate = new Date();
        }
        pagingData = { dateXOffset: 0, date: thisDate }

        repaint();
    };

    function repaintDatePanel(mutableDims, paging, zoomer) {
        datesView.layer.destroyChildren();
        var dateText = new Konva.Text({
            x: CONSTANTS.workflowNameMargin,
            y: 0,
            align: 'right',
            text: paging.date.toCelosUTCString(),
            fontSize: CONSTANTS.dateTextFontSize, fontStyle: 'bold'
        });
        datesView.layer.add(dateText);

        var aDate = new Date(paging.date.getTime());
        var x = mutableDims.cellDataOffset;
        while (x < datesView.stage.width()) {
            var aText = new Konva.Text({
                x: x,
                y: 0,
                width: CONSTANTS.baseCellWidth,
                align: 'center',
                text: zoomer.getDesc(aDate),
                fontSize: CONSTANTS.dateTextFontSize, fontStyle: 'bold'
            });
            datesView.layer.add(aText);
            aDate = zoomer.updateDateToNextStep(aDate);
            x += CONSTANTS.baseCellWidth;
        }

        datesView.layer.draw();
    }

    function repaintSlotStatePanel(mutableDims) {

        slotStatesView.layer.destroyChildren();
        var yOffset = 0;
        for (var i=0; i < rrmanModel.workflowGroups.length; i++) {
            var workflowGroupName = new Konva.Text({
                x: CONSTANTS.workflowNameMargin,
                y: yOffset + CONSTANTS.workflowNameYOffset,
                align: 'right',
                text: rrmanModel.workflowGroups[i].name,
                width: mutableDims.workflowNameWidth,
                fontSize: CONSTANTS.workflowGroupNameFontSize,
                fontStyle: 'bold'
            });
            yOffset += CONSTANTS.cellHeight;
            slotStatesView.layer.add(workflowGroupName);

            var entries = rrmanModel.workflowGroups[i].workflowToSlotsMap.entries();
            var next = entries.next();
            while (!next.done) {
                drawWorkflow(yOffset, next.value[0], next.value[1], mutableDims);
                yOffset += CONSTANTS.cellHeight;
                next = entries.next();
            }
            yOffset += CONSTANTS.cellHeight;
        }
        slotStatesView.layer.draw();
    }

    function updateCanvasSize() {
        return function() {
            var totalHeight = 0;
            for (var i=0; i < rrmanModel.workflowGroups.length; i++) {
                totalHeight += rrmanModel.workflowGroups[i].workflowToSlotsMap.count() * CONSTANTS.cellHeight;
                totalHeight += CONSTANTS.cellHeight * 2;
            }
            totalHeight -= CONSTANTS.cellHeight;

            slotStatesView.stage.setWidth(slotStatesView.panel.width() - CONSTANTS.scrollbarWidth);
            slotStatesView.stage.setHeight(totalHeight);
            slotStatesView.stage.draw();
        }
    }

    function isSlotStateCellBigEnough(slotStates) {
        var width = 0;
        for (var i = 0; i < slotStates.length; i++) {
            var cellSizeFactor = slotStates[i].expectedDuration / zoomers[currentZoom].baseCellDuration;
            width += cellSizeFactor * CONSTANTS.baseCellWidth;
        }
        return width / slotStates.length >= CONSTANTS.cellMinimalWidth;
    }

    function drawWorkflow(yOffset, workflow, slotStates, mutableDims) {

        var workflowName = new Konva.Text({
            x: CONSTANTS.workflowNameMargin, y: yOffset + CONSTANTS.workflowNameYOffset,
            align: 'right', text: workflow.id, width: mutableDims.workflowNameWidth,
            fontSize: CONSTANTS.workflowNameFontSize
        });

        slotStatesView.layer.add(workflowName);

        if (isSlotStateCellBigEnough(slotStates)) {
            drawSlotsStates(yOffset, slotStates, mutableDims);
        } else {
            drawWorkflowSlot(yOffset, slotStatesView.stage.width(), workflow, mutableDims);
        }
    }

    function drawSlotsStates(yOffset, slotStates, mutableDims) {
        var x = mutableDims.cellDataOffset;
        var slotStatesSeq = new Immutable.Seq(slotStates);
        var iterator = slotStatesSeq.filter( function(x) { return x.date.getTime() <= pagingData.date.getTime() }).values();
        var next = iterator.next();
        while (!next.done) {
            var slotState = next.value;
            if (x <= slotStatesView.stage.getWidth()) {
                var cellSizeFactor = slotState.expectedDuration / zoomers[currentZoom].baseCellDuration;
                var width = cellSizeFactor * CONSTANTS.baseCellWidth;

                var color = slotState.status === FAILED ? CONSTANTS.cellColorFailed : CONSTANTS.cellColorSuccess;

                var rect = new Konva.Rect({
                    x: x, y: yOffset, width: width, height: CONSTANTS.cellHeight,
                    fill: color, stroke: 'white', strokeWidth: 1
                });

                rect.object = slotState;
                slotStatesView.layer.add(rect);
            }
            x += width;
            next = iterator.next();
        }
    }

    function drawWorkflowSlot(y, width, workflow, mutableDims) {

        var rect = new Konva.Rect({
            x: mutableDims.cellDataOffset, y: y, width: width, height: CONSTANTS.cellHeight,
            fill: CONSTANTS.cellColorSuccess, stroke: 'white', strokeWidth: 1,
            transformsEnabled: 'position'
        });
        rect.object = workflow;
        workflow.shape = rect;

        slotStatesView.layer.add(rect);

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

function RrmanModel() {
    var startTime = new Date();
    this.startTime = startTime;
    var endTime = distractMs(startTime, HOUR_MS * 24 * 7);
    this.endTime = endTime;

    this.workflowGroups = (function() {


        var slotLengths = [5 * MINUTE_MS, 30 * MINUTE_MS, HOUR_MS, HOUR_MS];
        var wfGroups = [];
        for (var i=0; i < 5; i++) {

            var workflowToSlots = [];
            for (var j=0; j <= i; j++) {
                var wf = new Workflow("wf-" + i + "" + j, "WF #" + i + "" + j + ": every 5 minutes", "John Doe");
                var slotStates = generateSlotStates(wf, startTime, endTime, slotLengths[j % slotLengths.length]);
                workflowToSlots.push([wf, slotStates]);
            }
            var wfGroup = new WorkflowGroup("Group #" + i, new Immutable.OrderedMap(workflowToSlots));
            wfGroups.push(wfGroup);
        }
        return wfGroups;

        function generateSlotStates(workflow, startTime, endTime, duration) {
            var slotStates = [];
            var time = startTime;
            while (time.getTime() > endTime.getTime() ) {
                var status = Math.random() > 0.3 ? SUCCESS : FAILED;
                slotStates.push(new SlotState(status, workflow, time, duration));
                time = distractMs(time, duration);
            }
            return slotStates;
        }


    })();

    function distractMs(time, ms) {
        return new Date(time.getTime() - ms);
    }

}

