define(["app/utils", "app/constants", "lib/immutable"], function (Utils, Const, Immutable) {

    var STATUSES = {};

    function addStatus(name, color, selectedColor, abbreviation) {
        function Status(name, color) {
            this.name = name;
            this.color = color;
            this.selectedColor = selectedColor;
            this.abbrevation = abbreviation;
            this.abbrevationWidth = Utils.getTextWidth(abbreviation, Const.slotStateAbbreviationFontSize);
        }

        STATUSES[name] = new Status(name, color);
    }

    addStatus('READY', '#ffc', '#ffa', 'rdy');
    addStatus('RUNNING', '#ffc', '#ffa', 'run');
    addStatus('SUCCESS', '#cfc', '#afa', 'succ');
    addStatus('FAILURE', '#fcc', '#faa', 'fail');
    addStatus('WAITING', '#ccf', '#aaf', 'wait');
    addStatus('WAIT_TIMEOUT', '#ccf', '#aaf', 'tmut');

    function SlotState(status, workflow, date, externalID, retryCount) {
        this.status = status;
        this.workflow = workflow;
        this.date = date;
        this.externalID = externalID;
        this.retryCount = retryCount;
        this.selected = false;
    }

    SlotState.prototype.getSlotID = function () {
        return this.workflow.id + '@' + this.date.toISOString();
    };

    SlotState.prototype.getDescription = function () {
        return 'Slot ' + this.getSlotID() + ': ' + this.status.name;
    };

    SlotState.prototype.getColor = function () {
        if (this.selected) {
            return this.status.selectedColor;
        } else {
            return this.status.color;
        }
    };

    SlotState.prototype.repaint = function () {
        if (this.rect) {
            this.rect.setFill(this.getColor());
            this.rect.draw();
        }
        if (this.abbr) {
            this.abbr.setText(this.status.abbrevation);
            this.abbr.draw();
        }
    };

    SlotState.prototype.canBeRestarted = function () {
        return (this.status == STATUSES['FAILURE'] || this.status == STATUSES['WAIT_TIMEOUT'] || this.status == STATUSES['SUCCESS']);
    };

    function Workflow(id, info) {
        this.id = id;
        this.info = info;
    }

    Workflow.prototype.getDescription = function () {
        return 'Workflow ' + this.id;
    };

    function Model(uiConfig, workflowToSlotMap, _requestDate, _showDate) {
        this.uiConfig = uiConfig;
        this.workflowToSlotMap = workflowToSlotMap;
        var idsToWf = workflowToSlotMap.reduce(function (accum, slots, workflow) {
            accum.push([workflow.id, workflow]);
            return accum;
        }, []);
        this.workflowByName = new Immutable.Map(idsToWf);
        this.requestDate = _requestDate;
        this.showDate = _showDate;
    }


    return {
        STATUSES: STATUSES,
        SlotState: SlotState,
        Workflow: Workflow,
        Model: Model
    }

});