#!/usr/bin/env bash

LIBS=""
LIBS+=" ../../main/webapp/static/node_modules/react/dist/react.js"
LIBS+=" node_modules/react-dom/dist/react-dom-server.js"
LIBS+=" ../../main/webapp/static/node_modules/immutable/dist/immutable.js"
LIBS+=" ../../main/webapp/static/node_modules/events/events.js"
LIBS+=" node_modules/js-promise/js-promise.js"
LIBS+=" node_modules/object-assign/index.js"
LIBS+=" ../../main/webapp/static/js/lib.js"
LIBS+=" ../../main/webapp/static/js/Nav.js"
LIBS+=" ../../main/webapp/static/js/Dispatcher.js"
LIBS+=" ../../main/webapp/static/js/stores/sidebarStore.js"
LIBS+=" ../../main/webapp/static/js/stores/slotsStore.js"
LIBS+=" ../../main/webapp/static/js/components/Sidebar.js"
LIBS+=" ../../main/webapp/static/js/components/ContextMenu.js"
LIBS+=" ../../main/webapp/static/js/components/ModalBox.js"
LIBS+=" ../../main/webapp/static/js/components/SlotsTable.js"
LIBS+=" ../../main/webapp/static/js/components/Main.js"

jjs prepare.js ${LIBS} ololo.js
