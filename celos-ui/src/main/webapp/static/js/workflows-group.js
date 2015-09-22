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
    loadCommentsFromServer: function (props) {
        console.log("loadCommentsFromServer:", props)
        $.ajax({
            url: "/react",
            data: {
                count: slotsNum,
                group: props.name,
                zoom: props.request.zoom,
                time: props.request.time
            },
            dataType: 'json',
            cache: false,
            success: function (data) {
                this.setState({data: data});
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(props.url, status, err.toString());
            }.bind(this)
        });
    },
    componentWillMount: function () {
        console.log("componentWillMount:", this.props)
        this.loadCommentsFromServer(this.props);
    },
    componentWillReceiveProps: function (nextProps) {
        console.log("componentWillReceiveProps:", nextProps)
        this.loadCommentsFromServer(nextProps)
    },
    render: function () {
        console.log("WorkflowsGroupFetch", this.state);
        if (this.state) {
            return (
                <WorkflowsGroup data={this.state.data} request={this.props.request} />
            );
        } else {
            return <div />;
        }
    }
});


var WorkflowsGroup = React.createClass({
    render: function () {
        console.log("WorkflowsGroup", this.props.data);
        var req = this.props.request
        var groupName = this.props.data.name
        var newGroups
        if (req.groups && req.groups != []) {
            newGroups = req.groups.filter(function(x) { return x != groupName })
        } else {
            newGroups = [groupName]
        }
        var newUrl = makeCelosHref(req.zoom, req.time, newGroups)
        return (
            <table className="workflowTable">
                <thead>
                <tr>
                    <th className="groupName">
                    <a href={ newUrl }>
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
                <a href={this.props.data.url} >
                   {(! this.props.data.quantity)
                   ? <div />
                   : <div>{this.props.data.quantity}</div> }
                </a>
            </td>
        );
    }
});

