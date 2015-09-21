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

var slotsNum = Math.trunc(($(window).width() - 250) / (30 + 4)) - 1;

var WorkflowsGroupFetch = React.createClass({
    getInitialState: function () {
        return {data: null};
    },
    loadCommentsFromServer: function () {
        $.ajax({
            url: this.props.url,
            data: {
                count: slotsNum,
                zoom: getQueryVariable("zoom", window.location.search),
                time: getQueryVariable("time", window.location.search)
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
    componentWillReceiveProps: function () {
        this.loadCommentsFromServer()
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
        return (
            <table className="workflowTable">
                <thead>
                <tr>
                    <th className="groupName">
                    <a href={"#groups/" + encodeURIComponent(this.props.data.name) }>
                        {this.props.data.name}
                    </a>
                    </th>

                    {this.props.data.times.slice(- slotsNum).map(function (tt, i) {
                        return <th className="timeHeader" key={i}>{tt}</th>;
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
                <th className="workflowName">
                {this.props.data.workflowName}
                </th>

                {this.props.data.slots.slice(- slotsNum).map(function(slot, i) {
                    return <TimeSlot data={slot} key={i} />;
                })}
            </tr>
        );
    }
});

var TimeSlot = React.createClass({
    render: function () {
        return (
            <td className={"slot " + this.props.data.status}>
                <a href={this.props.data.url} data-slot-id="parquetify-retarget@2015-09-15T18:00:00.000Z">
                   {(! this.props.data.quantity)
                   ? <div />
                   : <div>{this.props.data.quantity}</div> }
                </a>
            </td>
        );
    }
});
