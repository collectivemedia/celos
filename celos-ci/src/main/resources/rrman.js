const CANVAS_WIDTH = 1024;
const CANVAS_HEIGHT = 768;
const CELL_HEIGHT = 36;

const SECONDS_MS = 1000;
const MINUTE_MS = 60 * SECONDS_MS;
const HOUR_MS = 60 * MINUTE_MS;

const START_TIME = new Date();
const END_TIME = distractMs(START_TIME, HOUR_MS * 24 * 7);

const BASE_CELL_WIDTH = 69;
var baseCellTimeIndex = 3;

const BASE_CELL_TIMES = [5 * MINUTE_MS, 15 * MINUTE_MS, 30 * MINUTE_MS, HOUR_MS, 2 * HOUR_MS];

const MINIMAL_WIDTH = 3;
const ZOOM_TOO_LOW_STROKE_MARGIN = 5;
const ZOOM_TOO_LOW_STROKE_STEP = 5;
const VERTICAL_MARGIN = 5;
var drawingContext;
var canvasElement;

function SlotState(status, workflow, time, duration) {
    this.status = status;
    this.workflow = workflow;
    this.time = time;
    this.duration = duration;
}

function Workflow(id, description, author) {
    this.id = id;
    this.description = description;
    this.author = author;
}

function WorkflowInfo(workflow, slotStates) {
    this.workflow = workflow;
    this.slotStates = slotStates;
}

function distractMs(time, ms) {
    return new Date(time.getTime() - ms);
}

var workflowInfos = [];

function init() {
    canvasElement = document.createElement("canvas");
    canvasElement.id = "rrman_canvas";
    document.getElementById("dashboardPanel").appendChild(canvasElement);

    canvasElement.width = CANVAS_WIDTH;
    canvasElement.height = CANVAS_HEIGHT;
    drawingContext = canvasElement.getContext("2d");

    canvasElement.addEventListener('mousemove', onCanvasMouse, false);

    var workflow = new Workflow("workflow-1", "WF #1: every 5 minutes", "John Doe");
    var slotStates = generateSlotStates(workflow, START_TIME, END_TIME, 5 * MINUTE_MS);
    var workflowInfo = new WorkflowInfo(workflow, slotStates);
    workflowInfos.push(workflowInfo);

    workflow = new Workflow("workflow-2", "WF #2: every 30 minutes", "John Doe");
    slotStates = generateSlotStates(workflow, START_TIME, END_TIME, 30 * MINUTE_MS);
    workflowInfo = new WorkflowInfo(workflow, slotStates);
    workflowInfos.push(workflowInfo);

    workflow = new Workflow("workflow-3", "WF #3: every hour", "John Doe");
    slotStates = generateSlotStates(workflow, START_TIME, END_TIME, HOUR_MS);
    workflowInfo = new WorkflowInfo(workflow, slotStates);
    workflowInfos.push(workflowInfo);

    workflow = new Workflow("workflow-4", "WF #4: every minute", "John Doe");
    slotStates = generateSlotStates(workflow, START_TIME, END_TIME, MINUTE_MS);
    workflowInfo = new WorkflowInfo(workflow, slotStates);
    workflowInfos.push(workflowInfo);

    document.getElementById("zoomIn").addEventListener("click", zoomIn);
    document.getElementById("zoomOut").addEventListener("click", zoomOut);

    drawBoard(workflowInfos);

}

function zoomIn() {
    if (baseCellTimeIndex > 0) {
        baseCellTimeIndex--;
    }
    drawBoard(workflowInfos);
}

function zoomOut() {
    if (baseCellTimeIndex < BASE_CELL_TIMES.length - 1) {
        baseCellTimeIndex++;
    }
    drawBoard(workflowInfos);
}

function onCanvasMouse(event) {
    var rect = canvasElement.getBoundingClientRect();
    var x = event.clientX - rect.left;
    var y = event.clientY - rect.top;

    var obj = getWorkflowObjectByCoords(x, y, workflowInfos);
    console.log(obj);
}

function generateSlotStates(workflow, startTime, endTime, duration) {
    var slotStates = [];
    var time = startTime;
    while (time.getTime() > endTime.getTime() ) {
        slotStates.push(new SlotState(null, workflow, time, duration));
        time = distractMs(time, duration);
    }
    return slotStates;
}

var canvasDrawer = {
    drawCell: function drawCell(slotState, xOffset, yOffset, width, height)
    {
        drawingContext.beginPath();
        drawingContext.rect(xOffset, yOffset, width, height);
        drawingContext.fillStyle = 'green';
        drawingContext.fill();

        drawingContext.lineWidth = 1;
        drawingContext.strokeStyle = 'white';
        drawingContext.stroke();
    },
    drawSolidDashedCell: function (workflowInfo, yOffset, expWidth) {
        drawingContext.beginPath();
        drawingContext.rect(0, yOffset, expWidth, CELL_HEIGHT);
        drawingContext.fillStyle = 'green';
        drawingContext.fill();

        drawingContext.beginPath();
        var stepY = CELL_HEIGHT - ZOOM_TOO_LOW_STROKE_MARGIN * 2;
        var stepX = ZOOM_TOO_LOW_STROKE_STEP;
        var x = -ZOOM_TOO_LOW_STROKE_MARGIN;
        var y = yOffset + CELL_HEIGHT - ZOOM_TOO_LOW_STROKE_MARGIN;
        while (x + stepX < expWidth) {
            drawingContext.moveTo(x, y);
            drawingContext.lineTo(x + stepX, y - stepY);
            x += stepX;
        }
        drawingContext.strokeStyle = "#FFF";
        drawingContext.stroke();
    }
};

function CollideDrawer(x, y) {
    this.x = x;
    this.y = y;
}

CollideDrawer.prototype.drawCell = function(slotState, xOffset, yOffset, width, height) {
    if (this.x >= xOffset && this.x <= xOffset + width &&
        this.y >= yOffset && this.y <= yOffset + height) {
        this.foundObject = slotState;
    }
};

CollideDrawer.prototype.drawSolidDashedCell = function (workflowInfo, yOffset) {
    if (this.y >= yOffset && this.y <= yOffset + CELL_HEIGHT + VERTICAL_MARGIN) {
        this.foundObject = workflowInfo;
    }
};

function getWorkflowObjectByCoords(x, y, workflowInfos) {
    var collideDrawer = new CollideDrawer(x, y);
    canvasWorkflowsProcessing(collideDrawer)(workflowInfos);
    return collideDrawer.foundObject;
}

drawBoard = (function() {
    var drawWithCanvasDrawer = canvasWorkflowsProcessing(canvasDrawer);
    return function(workflowInfos) {
        drawingContext.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        drawWithCanvasDrawer(workflowInfos);
    }
})();

function canvasWorkflowsProcessing(drawer) {

    function expectedWidth(slotStates) {
        var width = 0;
        for (var i = 0; i < slotStates.length; i++) {
            var cellSizeFactor = slotStates[i].duration / BASE_CELL_TIMES[baseCellTimeIndex];
            width += cellSizeFactor * BASE_CELL_WIDTH;
        }
        return width;
    }

    function drawWorkflow(yOffset, workflowInfo) {
        var slotStates = workflowInfo.slotStates;
        var expWidth = expectedWidth(slotStates);
        if (expWidth / slotStates.length >= MINIMAL_WIDTH) {
            var xOffset = 0;
            for (var i = 0; i < slotStates.length; i++) {
                var cellSizeFactor = slotStates[i].duration / BASE_CELL_TIMES[baseCellTimeIndex];
                var width = cellSizeFactor * BASE_CELL_WIDTH;
                drawer.drawCell(slotStates[i], xOffset, yOffset, width, CELL_HEIGHT);
                xOffset += width;
            }
        } else {
            drawer.drawSolidDashedCell(workflowInfo, yOffset, expWidth);
        }
    }

    return function(workflowInfos) {
        var yOffset = 0;
        for (var i = 0; i < workflowInfos.length; i++) {
            drawWorkflow(yOffset, workflowInfos[i]);
            yOffset += CELL_HEIGHT + VERTICAL_MARGIN;
        }

    }
}