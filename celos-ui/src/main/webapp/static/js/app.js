/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */



var CelosMainFetch = React.createClass({
    getInitialState: function () {
        return {data: {rows: [], navigation: {}}}
    },
    loadCommentsFromServer: function (props) {
//        console.log("loadCommentsFromServer " + props.request.zoom + " " + props.request.time)
        $.ajax({
            url: props.url,
            data: {
                zoom: props.request.zoom,
                time: props.request.time
            },
            dataType: 'json',
            cache: false,
            success: function (data) {
                this.setState({data: data})
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(props.url, status, err.toString())
            }.bind(this)
        })
    },
    componentWillMount: function () {
        this.loadCommentsFromServer(this.props)
    },
    componentWillReceiveProps: function (nextProps) {
        this.loadCommentsFromServer(nextProps)
    },
    render: function () {
        console.log("CelosMainFetch", this.props, this.state)
        var tmp = this.state.data
        if (this.props.request.groups) {
            var groupFilter = this.props.request.groups
            tmp.rows.forEach(function(x) {
                x.active = (groupFilter.indexOf(x.name) >= 0)
            })
        } else {
            tmp.rows.forEach(function(x) {
                x.active = true
            })
        }
        return (
            <CelosMain data={tmp} request={this.props.request} />
        )
    }
})

var CelosMain = React.createClass({
    render: function () {
        console.log("CelosMain", this.props.data, this.props.request)

        return (
            <div>
                <h2>{this.props.data.currentTime}</h2>

                <Navigation data={this.props.data.navigation} request={this.props.request} />

                {this.props.data.rows.map(function (wfGroup, i) {
                    if (wfGroup.active) {
                        return (
                        <div key={i}>
                            <WorkflowsGroupFetch name={wfGroup.name} active={wfGroup.active} request={this.props.request} />
                            <br />
                        </div>
                        )
                    } else {
                        var req = this.props.request
                        var newUrl = makeCelosHref(req.zoom, req.time, req.groups.concat(wfGroup.name))
                        return (
                        <div key={i}>
                        <a href={ newUrl }>{wfGroup.name}</a>
                        </div>
                        )
                    }
                }.bind(this))}
            </div>
        )
    }

})


var Navigation = React.createClass({
    render: function () {
        console.log("Navigation", this.props.data)
        return (
            <center className="bigButtons">
                <a href={makeCelosHref(this.props.request.zoom, this.props.data.left,  this.props.request.groups)}>&lt; Prev page </a>
                <strong> | </strong>
                <a href={makeCelosHref(this.props.request.zoom, this.props.data.right, this.props.request.groups)}>Next page &gt; </a>
            <br />
            <br />
                <a href={makeCelosHref(this.props.data.zoomOut, this.props.request.time, this.props.request.groups)}>Zoom OUT {this.props.data.zoomOut} </a>
                <strong> / </strong>
                <a href={makeCelosHref(this.props.data.zoomIn,  this.props.request.time, this.props.request.groups)}>Zoom IN {this.props.data.zoomIn} </a>
            <br />
            <br />
            </center>
        )
    }
})


var parseParams = function (paramsList) {
    res = {}
    paramsList.forEach(function (parameter) {
        if (parameter === "") {
            // pass
        } else if (parameter.startsWith("groups=")) {
            res["groups"] = parameter.substring(("groups=").length).split(",").map(decodeURIComponent).filter(function(x) {return x})
        } else if (parameter.startsWith("zoom=")) {
            res["zoom"] = decodeURIComponent(parameter.substring(("zoom=").length))
        } else if (parameter.startsWith("time=")) {
            res["time"] = decodeURIComponent(parameter.substring(("time=").length))
        } else {
            throw "Unknown parameter: " + parameter
        }
    })
    return res
}

var defaultController = function() {
    if (window.location.hash === "" || window.location.hash === "#ui") {
        ReactDOM.render(
            <CelosMainFetch url="/react" request={{}} />,
            document.getElementById('content')
        )
    } else if (window.location.hash.startsWith("#ui?")) {
        params = parseParams(window.location.hash.substring("#ui?".length).split("&"))
        request = {groups: params.groups, zoom: params.zoom, time: params.time}
        ReactDOM.render(
            <CelosMainFetch url={"/react"} request={ request } />,
            document.getElementById('content')
        )

    } else if (window.location.hash.indexOf("#test") === 0) {
        ReactDOM.render(
            <CelosMainFetch url="assets/main.json" />,
            document.getElementById('content')
        )
    } else {
        throw "no route for this URL: " + window.location.hash
    }
}


window.addEventListener('hashchange', function() {
    console.log("URL:", window.location.hash)
    defaultController()
})
defaultController()


