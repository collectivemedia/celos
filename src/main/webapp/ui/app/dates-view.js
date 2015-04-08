define(['app/managers', 'app/utils', 'app/constants', 'lib/konva'], function(Managers, Utils, Const, Konva) {

    function View(datesView, zoomer) {

        var _this = this;

        function repaintDatePanel() {
            datesView.layer.destroyChildren();
            var dateText = new Konva.Text({
                x: Const.workflowNameMargin,
                y: 0,
                align: 'right',
                text: Utils.toCelosUTCString(zoomer.getPagingOffsetDate()),
                fontSize: Const.dateTextFontSize, fontStyle: 'bold'
            });
            datesView.layer.add(dateText);

            var mainDateWidth = Utils.getTextWidth(Const.SAMPLE_DATE_DAY, Const.dateTextFontSize);
            var width = Utils.getTextWidth(Const.SAMPLE_LINE_TIME, Const.dateTextFontSize);

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
                            text: Utils.toDayString(midnightPass),
                            fontSize: Const.dateTextFontSize, fontStyle: 'bold'
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
                    fontSize: Const.dateTextFontSize, fontStyle: 'bold'
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

    }

    return View;

});