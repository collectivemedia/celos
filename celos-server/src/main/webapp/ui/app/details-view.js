define(['app/managers', 'app/utils', 'app/constants', 'lib/konva'], function(Managers, Utils, Const, Konva) {

    function DetailsView(detailsCanvas, parent) {

        this.repaint = function() {
            detailsCanvas.layer.destroyChildren();

            var selectedSlots = parent.getSelectionManager().slotsIterator();
            var next = selectedSlots.next();

            var x = 0;
            var y = 0;
            var newHeight = 0;
            while (!next.done) {
                var slotState = next.value;
                var properties = {
                    x: x, y: y, textColor: 'red', backgroundColor: 'green',
                    width: detailsCanvas.stage.getWidth(), align: 'left', text: slotState.getSlotID(),
                    fontSize: 16, fontStyle: 'bold'
                };
                if (slotState.externalID) {
                    properties.fill = 'blue';
                    properties.textDecoration = 'underline';
                }
                var aText = new Konva.Text(properties);
                if (slotState.externalID) {
                    aText.on('mouseover', function () {
                        document.body.style.cursor = 'pointer';
                    });
                    aText.on('mouseout', function () {
                        document.body.style.cursor = 'default';
                    });
                    aText.on('click', function () {
                        var url = parent.getModel().uiConfig.hueURL.replace("${EXTERNAL_ID}", slotState.externalID);
                        window.open(url, '_blank');
                    });
                }
                detailsCanvas.layer.add(aText);
                next = selectedSlots.next();
                y += aText.getHeight();
                newHeight = y;
            }
            detailsCanvas.stage.setHeight(newHeight);
            detailsCanvas.layer.batchDraw();

        };

    }

    return DetailsView;

});