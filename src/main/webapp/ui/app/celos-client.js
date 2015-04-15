define(['app/utils', 'app/domain', 'app/constants', 'lib/immutable'], function (Utils, Domain, Const, Immutable) {

    function CelosClient() {
        this.getConfig = function (onGetConfig) {
            $.ajax({
                dataType: "json",
                url: "ui-config",
                success: onGetConfig
            });
        };

        this.rerunSlot = function (slot) {
            $.ajax({
                type: "POST",
                url: "rerun",
                data: {time: Utils.toCelosUTCString(slot.date), id: slot.workflow.id},
                success: function () {
                    $.ajax({
                        dataType: "json",
                        data: {time: Utils.toCelosUTCString(slot.date), id: slot.workflow.id},
                        url: "slot-state",
                        success: function (data) {
                            slot.status = Domain.STATUSES[data.status];
                            slot.externalID = data.externalID;
                            slot.repaint();
                        }
                    })
                }
            });
        };

        this.update = function (showDate) {
            var requestDate = showDate;
            if (!requestDate) {
                requestDate = new Date();
            }
            this.getConfig(function (config) {
                var forDate = Utils.addMs(requestDate, 3 * Const.DAY_MS);
                $.ajax({
                    dataType: "json",
                    data: {time: Utils.toCelosUTCString(forDate)},
                    url: "workflow-slots",
                    success: function (workflowsData) {
                        onWorkflowsData(workflowsData, config, requestDate, showDate);
                    }
                })
            });
        };

        function onWorkflowsData(workflowsData, uiConfig, requestDate, showDate) {

            var wgContent = [];
            for (var i = 0; i < workflowsData.workflows.length; i++) {
                var wfData = workflowsData.workflows[i];

                var workflow = new Domain.Workflow(wfData.id, wfData.info);

                var slotStatesArr = [];
                for (var j = 0; j < wfData.slots.length; j++) {
                    var slot = wfData.slots[j];
                    var date = new Date(slot.time);
                    var slotState = new Domain.SlotState(Domain.STATUSES[slot.status], workflow, date, slot.externalID, slot.retryCount);
                    slotStatesArr.push([date, slotState]);
                }
                var slotStates = Immutable.OrderedMap(slotStatesArr);
                wgContent.push([workflow, slotStates]);
            }
            var model = new Domain.Model(uiConfig, new Immutable.Map(wgContent), requestDate, showDate);
            $(document).trigger("rrman:model_updated", model);
        }
    }

    return CelosClient;

});