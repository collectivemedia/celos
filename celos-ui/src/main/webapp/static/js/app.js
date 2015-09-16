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


var WorkflowsGroup = React.createClass({
    render: function () {
        console.log("XXX", this.props.data.rows);
        return (
            <table>
                <thead>
                <tr>
                    <th className="workflowGroup">{this.props.data.name}</th>
                    {this.props.data.times.map(function (tt, i) {
                        return <th key={i}>{tt}</th>;
                    })}
                </tr>
                </thead>
                <tbody>
                {this.props.data.rows.map(function (product, key) {
                    return <ProductRow data={product} key={key}/>;
                })}
                </tbody>
            </table>
        );
    }
});


var ProductRow = React.createClass({
    render: function () {
        return (
            <tr>
                <th scope="row" className="workflow">{this.props.data.wf}</th>
                <td className="slot WAITING">wait</td>
                <td className="slot SUCCESS">2</td>
                <td className="slot SUCCESS">2</td>
                <td className="slot SUCCESS">2</td>
                <TimeSlot
                    url="http://cldmgr001.ewr004.collective-media.net:8888/oozie/list_oozie_workflow/0000392-150915121135558-oozie-oozi-W"/>
                <TimeSlot
                    url="http://cldmgr001.ewr004.collective-media.net:8888/oozie/list_oozie_workflow/0000392-150915121135558-oozie-oozi-W"/>
                <TimeSlot
                    url="http://cldmgr001.ewr004.collective-media.net:8888/oozie/list_oozie_workflow/0000392-150915121135558-oozie-oozi-W"/>
                <TimeSlot
                    url="http://cldmgr001.ewr004.collective-media.net:8888/oozie/list_oozie_workflow/0000392-150915121135558-oozie-oozi-W"/>
            </tr>
        );
    }
});

var TimeSlot = React.createClass({
    render: function () {
        return (
            <td>
            <span className="label label-default">
                <a href={this.props.url} className="slotLink"
                   data-slot-id="parquetify-retarget@2015-09-15T18:00:00.000Z">&nbsp;&nbsp;&nbsp;&nbsp;</a>
            </span>
            </td>
        );
    }
});


console.log("four:", window.location.hash);


routie('', function () {
    //this gets called when hash == #hello
    ReactDOM.render(
        <CelosMainFetch url="assets/main.json" pollInterval={0}/>,
        document.getElementById('content')
    )
});


routie('*', function () {

    alert("ERROR: wrong route!")

});
