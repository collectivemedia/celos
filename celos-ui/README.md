# Celos UI overview

Celos UI has two components:
* Java app as static resources server and Celos API proxy.
* React.js single page application

## React.js application

Application entry point is ./celos-ui/src/main/webapp/static/index.html file.
Cool thing in react.js is that you can always render page from dumped state,
because react render is pure function.
React.js doesn't depend on jquery or underscore, so all thirdparty functions has been placed to lib.js.
We also don't use ES6 or JSX, because it needs external compilers.


### Dispatcher

Dispatcher.js does all event routing for each registered component.

Events:
* LOAD_GROUPS
* TODO_UPDATE
* RECTANGLE_UPDATE
* LOAD_SLOTS
* FOCUS_ON_SLOT
* CLEAR_SELECTION
* LOAD_NAVIGATION
* MODAL_BOX


### Stores

Stores work with application model and state changes.
There are two singleton components: sidebarStore.js and slotsStore.js.
Each state change causes by Dispatcher event.
State is a singleton object from Immutable.js library.
You have to call "this.emit(CHANGE_EVENT)" after each state change to render the page.


### Views

App has several react components:
* ContextMenu.js
* Main.js
* ModalBox.js
* Nav.js
* Sidebar.js
* SlotsTable.js

Pages render starts from page root, because react touches only changed components when you use Immutable collections.


#### Tests

All tests runs from java using Rhino js engine.
For test we need additional libraries, i.e. engine doesn't have all functionality.

List of tests:
* EventSystemTest
* StaticRenderTest






