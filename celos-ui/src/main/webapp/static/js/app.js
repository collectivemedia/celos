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
        return {data: {rows: []}};
    },
    loadCommentsFromServer: function () {
        $.ajax({
            url: this.props.url,
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
        console.log("CelosMainFetch", this.state.data);
        return (
            <CelosMain data={this.state.data}/>
        );
    }
});

var CelosMain = React.createClass({
    render: function () {
        console.log("CelosMain", this.props.data);

        var rows = [];
        this.props.data.rows.forEach(function (wfGroup) {
            rows.push(<WorkflowsGroupFetch url={wfGroup.url} key={wfGroup.key}/>);
        }.bind(this));
        return (
            <div>
                <h2>{this.props.data.currentTime}</h2>
                {rows}
            </div>
        );
    }

});

var WorkflowsGroupFetch = React.createClass({
    getInitialState: function () {
        return {data: null};
    },
    loadCommentsFromServer: function () {
        $.ajax({
            url: this.props.url,
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
        console.log("WorkflowsGroupFetch", this.state.data);
        if (this.state.data == null) {
            return <div />;
        } else {
            return (
                <WorkflowsGroup data={this.state.data}/>
            );
        }
    }
});



console.log("four:", window.location.hash);


routie('', function () {
    //this gets called when hash == #hello
    ReactDOM.render(
        <CelosMainFetch url="/react" pollInterval={0}/>,
        document.getElementById('content')
    )
});

routie('test', function () {
    //this gets called when hash == #test
    ReactDOM.render(
        <CelosMainFetch url="assets/main.json" pollInterval={0}/>,
        document.getElementById('content')
    )
});

routie('*', function () {

    alert("ERROR: wrong route!")

});
