require(['app/main-view', 'app/domain', 'app/constants', 'app/celos-client', 'lib/konva', 'jquery', 'jquery-ui'],
function (MainView, Domain, Const, CelosClient, Konva, $) {

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


    var slotStatesContext = {
        activeLayer: slotStatesLayerActive,
        inactiveLayer: slotStatesLayerInactive,
        stage: slotStatesStage,
        panel: slotStatesPanel
    };
    var dateContext = {layer: dateLayer, stage: dateStage, panel: datePanel};
    var detailsContext = {layer: detailsLayer, stage: detailsStage, panel: detailsPanel};


    var view = new MainView(slotStatesContext, dateContext, detailsContext);
    var client = new CelosClient(view);

    $(document).on("rrman:model_updated", function (evt, model) {
        view.setModel(model);
        view.repaintAll();
    });
    $(document).on("rrman:request_data", function (evt, date) {
        client.update(date);
    });
    $(document).on("rrman:slot_rerun", function (evt, slot) {
        client.rerunSlot(slot);
    });
    window.addEventListener('resize', view.onResize, false);


    view.setupEventListeners();
    view.triggerUpdate();

    setInterval(function () {
        view.triggerUpdate();
    }, Const.REFRESH_INTERVAL);

    $(function () {
        $("#datepicker").datepicker();
        $("button").button();
    });

});