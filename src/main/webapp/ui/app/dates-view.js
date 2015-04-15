define(['app/utils', 'app/constants', 'lib/konva'], function(Utils, Const, Konva) {

    function DatesView(datesCanvas, parent) {

        var _this = this;

        this.updateCanvasSize = function () {
            datesCanvas.stage.setWidth(datesCanvas.panel.width() - Const.scrollbarWidth);
        };

        this.onResize = function () {
            _this.updateCanvasSize();
            _this.repaint()
        };

        this.repaint = function() {
            datesCanvas.layer.destroyChildren();
            var dateText = new Konva.Text({
                x: Const.workflowNameMargin,
                y: 0,
                align: 'right',
                text: Utils.toCelosUTCString(parent.getZoomer().getCurrentViewDate()),
                fontSize: Const.dateTextFontSize, fontStyle: 'bold'
            });
            datesCanvas.layer.add(dateText);

            var mainDateWidth = Utils.getTextWidth(Const.SAMPLE_DATE_DAY, Const.dateTextFontSize);
            var width = Utils.getTextWidth(Const.SAMPLE_LINE_TIME, Const.dateTextFontSize);

            var current = parent.getZoomer().getCurrentViewDate().getTime();
            var aDate = new Date(current - current % parent.getZoomer().getCurrentZoom().baseCellDuration);
            var metrics = parent.getMetricsByDate(aDate);
            var midnightPass = false;
            while (metrics.absoluteX < datesCanvas.stage.width()) {

                if (midnightPass) {
                    var midMetrics = parent.getMetricsByDate(midnightPass);
                    var x = midMetrics.absoluteX - mainDateWidth / 2;
                    if (x > parent.getMutableDims().cellDataOffset) {
                        var aText = new Konva.Text({
                            x: x,
                            y: 0,
                            align: 'center',
                            text: Utils.toDayString(midnightPass),
                            fontSize: Const.dateTextFontSize, fontStyle: 'bold'
                        });
                        datesCanvas.layer.add(aText);
                    }
                    midnightPass = false;
                }
                var aText = new Konva.Text({
                    x: metrics.absoluteX - width / 2,
                    y: 20,
                    align: 'center',
                    text: parent.getZoomer().getCurrentZoom().getDescription(aDate),
                    fontSize: Const.dateTextFontSize, fontStyle: 'bold'
                });
                var line = new Konva.Line({
                    points: [metrics.absoluteX, 40, metrics.absoluteX, dateText.getWidth()],
                    strokeWidth: 1,
                    stroke: 'black'
                });
                datesCanvas.layer.add(line);
                datesCanvas.layer.add(aText);
                var newDate = parent.getZoomer().getCurrentZoom().getDateAfterSteps(aDate, 1);
                if (newDate.getUTCDay() != aDate.getUTCDay()) {
                    midnightPass = aDate;
                }
                aDate = newDate;
                metrics = parent.getMetricsByDate(aDate);
            }

            datesCanvas.layer.batchDraw();
        }

    }

    return DatesView;

});