


if (!String.prototype.startsWith) {
  String.prototype.startsWith = function(searchString, position) {
    position = position || 0
    return this.indexOf(searchString, position) === position
  }
}



function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
            if (pair[0] == variable) {
                return pair[1];
            }
        }
    return null;
}

var makeCelosHref = function(zoom, time, groups) {
    var url0 = "#ui?"
    if (zoom) {
        url0 += "zoom=" + encodeURIComponent(zoom) + "&"
    }
    if (time) {
        url0 += "time=" + encodeURIComponent(time) + "&"
    }
    if (groups && groups.length != 0) {
        url0 += "groups=" + groups.map(encodeURIComponent).join(",") + "&"
    }
    return url0.substring(0, url0.length - 1)
}

