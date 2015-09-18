/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
        return {data: {rows: [], navigation: {}}};
    },
    loadCommentsFromServer: function () {
        $.ajax({
            url: this.props.url,
            data: {
                zoom: getQueryVariable("zoom"),
                time: getQueryVariable("time")
            },
            dataType: 'json',
            cache: false,
            success: function (data) {
                this.setState({data: data});
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    componentDidMount: function () {
        this.loadCommentsFromServer();
    },
    render: function () {
        console.log("CelosMainFetch", this.props);
        tmp = this.state.data;
        if (this.props.group) {
            console.log("CelosMainFetch2", this.props);
            var groupFilter = this.props.group;
            tmp.rows = tmp.rows.filter(function(x) {
                                      return groupFilter == x.name;
                                  });
        }
        console.log("CelosMainFetch3", this.props);
        return (
            <CelosMain data={tmp}/>
        );
    }
});

var CelosMain = React.createClass({
    render: function () {
        console.log("CelosMain", this.props.data);

        return (
            <div>
                <h2>{this.props.data.currentTime}</h2>

                <Navigation data={this.props.data.navigation} />

                {this.props.data.rows.map(function (wfGroup, i) {
                    return (
                    <div key={i}>
                        <WorkflowsGroupFetch url={wfGroup.url} />
                        <br />
                    </div>
                    );
                })}
            </div>
        );
    }

});

var Navigation = React.createClass({
    render: function () {
        console.log("Navigation", this.props.data);
        return (
            <center className="bigButtons">
                <a href={this.props.data.left + window.location.hash}> &lt; Prev page </a>
                <strong> | </strong>
                <a href={this.props.data.right + window.location.hash}> Next page &gt; </a>
            <br />
            <br />
                <a href={this.props.data.zoomOut + window.location.hash}> Zoom OUT </a>
                <strong> / </strong>
                <a href={this.props.data.zoomIn + window.location.hash}> Zoom IN </a>
            <br />
            <br />
            </center>
        );
    }
});


console.log("four:", window.location.hash);


routie({
    '': function () {
        ReactDOM.render(
            <CelosMainFetch url="/react" pollInterval={0}/>,
            document.getElementById('content')
        );
    },
    'test': function () {
        ReactDOM.render(
            <CelosMainFetch url="assets/main.json" pollInterval={0}/>,
            document.getElementById('content')
        );
    },

    'groups/:name': function (name) {
        ReactDOM.render(
            <CelosMainFetch url={"/react"} group={ name } pollInterval={0}/>,
            document.getElementById('content')
        );
    }
});
