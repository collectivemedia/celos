define(['app/utils', 'app/constants', 'lib/konva'], function(Utils, Const, Konva) {

    function View(slotStatesView, parent) {

        var _this = this;

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

        this.onResize = function () {
            _this.updateCanvasSize();
            _this.repaint()
        };

        this.repaint = function() {

            var oldLayer = slotStatesView.activeLayer;
            slotStatesView.activeLayer = slotStatesView.inactiveLayer;
            slotStatesView.inactiveLayer = oldLayer;

            var yOffset = 0;

            for (var i = 0; i < parent.getModel().uiConfig.workflowToSlotMap.length; i++) {
                var wfGroup = parent.getModel().uiConfig.workflowToSlotMap[i];
                var workflowGroupName = new Konva.Text({
                    x: Const.workflowNameMargin,
                    y: yOffset + Const.workflowNameYOffset,
                    align: 'right',
                    text: wfGroup.name,
                    width: parent.getMutableDims().workflowNameWidth,
                    fontSize: Const.workflowGroupNameFontSize,
                    fontStyle: 'bold'
                });
                yOffset += Const.cellHeight;
                slotStatesView.activeLayer.add(workflowGroupName);

                for (var j = 0; j < wfGroup.workflows.length; j++) {
                    var workflow = parent.getModel().workflowByName.get(wfGroup.workflows[j]);
                    drawWorkflow(yOffset, wfGroup.workflows[j], parent.getModel().workflowToSlotMap.get(workflow));
                    yOffset += Const.cellHeight;
                }
                yOffset += Const.cellHeight;
            }
            slotStatesView.stage.setHeight(yOffset - Const.cellHeight);

            slotStatesView.activeLayer.listening(true);
            slotStatesView.activeLayer.setVisible(true);
            slotStatesView.activeLayer.batchDraw();
            slotStatesView.inactiveLayer.listening(false);
            slotStatesView.inactiveLayer.setVisible(false);
            slotStatesView.inactiveLayer.destroyChildren();

        };

        function drawWorkflow(yOffset, workflow, slotStatesSeq) {

            var color;
            if (slotStatesSeq) {
                if (!drawSlots(yOffset, slotStatesSeq)) {
                    drawSolidSlot(yOffset, slotStatesView.stage.width() - parent.getMutableDims().cellDataOffset, workflow);
                }
                color = 'black';
            } else {
                color = 'red';
            }

            var workflowName = new Konva.Text({
                x: Const.workflowNameMargin, y: yOffset + Const.workflowNameYOffset,
                align: 'right', text: workflow, width: parent.getMutableDims().workflowNameWidth,
                fontSize: Const.workflowNameFontSize, fill: color
            });
            //slotStatesView.activeLayer.add(rect);
            slotStatesView.activeLayer.add(workflowName);

        }

        this.updateCanvasSize = function() {
            slotStatesView.stage.setWidth(slotStatesView.panel.width() - Const.scrollbarWidth);
        };

        function prepareSlots(yOffset, slotStatesMap) {

            var shapes = [];
            var slotStates = slotStatesMap.toArray();

            for (var i = 0; i < slotStates.length; i++) {
                var slotState = slotStates[i];
                var metrics = parent.getMetricsByDate(slotState.date);

                if (metrics.leftCut < Const.baseCellWidth && metrics.absoluteX <= slotStatesView.stage.getWidth()) {

                    var widthNoCut;
                    if (i + 1 < slotStates.length) {
                        var nextSlotMetrics = parent.getMetricsByDate(slotStates[i + 1].date);
                        var widthToNextSlot = nextSlotMetrics.absoluteX - metrics.absoluteX;
                        if (widthToNextSlot < Const.cellMinimalWidth) {
                            return false;
                        }
                        widthNoCut = Math.min(Const.baseCellWidth, widthToNextSlot);
                    } else {
                        widthNoCut = Const.baseCellWidth;
                    }

                    var widthToDraw = Math.max(0, widthNoCut - metrics.leftCut);
                    var rect = new Konva.Rect({
                        x: metrics.absoluteX + metrics.leftCut,
                        y: yOffset,
                        width: widthToDraw,
                        height: Const.cellHeight,
                        fill: slotState.getColor(),
                        stroke: 'white',
                        strokeWidth: 1
                    });
                    rect.slotState = slotState;
                    slotState.rect = rect;
                    var group = new Konva.Group();
                    group.add(rect);

                    if (slotState.status.abbrevation !== '' && widthToDraw > slotState.status.abbrevationWidth) {
                        var abbr = new Konva.Text({
                            x: metrics.absoluteX + metrics.leftCut,
                            y: yOffset + Const.workflowNameYOffset,
                            align: 'center',
                            width: widthToDraw,
                            text: slotState.status.abbrevation,
                            fontSize: Const.slotStateAbbreviationFontSize
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
                for (var i = 0; i < shapes.length; i++) {
                    slotStatesView.activeLayer.add(shapes[i]);
                }
                return true;
            } else {
                return false;
            }
        }

        function drawSolidSlot(y, width, workflow) {

            var rect = new Konva.Rect({
                x: parent.getMutableDims().cellDataOffset, y: y, width: width, height: Const.cellHeight,
                fill: Const.cellColorZoomMe, stroke: 'white', strokeWidth: 1,
                transformsEnabled: 'position'
            });
            //rect.object = workflow;
            //workflow.shape = rect;

            slotStatesView.activeLayer.add(rect);

            var dashing = new Konva.Shape({
                drawFunc: function (context) {
                    context.beginPath();

                    var stepY = Const.cellHeight - Const.ZOOM_TOO_LOW_STROKE_MARGIN * 2;
                    var stepX = Const.ZOOM_TOO_LOW_STROKE_STEP;

                    var lineX = parent.getMutableDims().cellDataOffset + Const.ZOOM_TOO_LOW_STROKE_MARGIN;
                    var lineY = y + Const.ZOOM_TOO_LOW_STROKE_MARGIN;// + Const.cellHeight - Const.ZOOM_TOO_LOW_STROKE_MARGIN;

                    var lineXTo = lineX - stepX * 2;
                    while (lineXTo < parent.getMutableDims().cellDataOffset + width) {
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

            var textWidth = Utils.getTextWidth(Const.zoomInDesrcText, Const.slotStateAbbreviationFontSize);

            var descrBackground = new Konva.Rect({
                x: parent.getMutableDims().cellDataOffset + (width - textWidth) / 2,
                y: y + Const.workflowNameYOffset,
                width: textWidth, height: Const.cellHeight - Const.workflowNameYOffset * 2,
                fill: Const.cellColorZoomMe, transformsEnabled: 'position'
            });

            slotStatesView.activeLayer.add(descrBackground);

            var descr = new Konva.Text({
                x: parent.getMutableDims().cellDataOffset + (width - textWidth) / 2,
                y: y + Const.workflowNameYOffset,
                width: textWidth,
                height: Const.cellHeight - Const.workflowNameYOffset * 2,
                align: 'center', text: Const.zoomInDesrcText,
                fontSize: Const.slotStateAbbreviationFontSize
            });

            slotStatesView.activeLayer.add(descr);
        }
    }

    return View;

});