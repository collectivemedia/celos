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
                <th scope="row" className="workflow">{this.props.data.workflowName}</th>
                {this.props.data.slots.map(function(slot, i) {
                    return <TimeSlot data={slot} />;
                })}
            </tr>
        );
    }
});

var TimeSlot = React.createClass({
    render: function () {
        return (
            <td>
            <span className={"slot " + this.props.data.status}>
                <a href={this.props.data.url} className="slotLink"
                   data-slot-id="parquetify-retarget@2015-09-15T18:00:00.000Z">&nbsp;&nbsp;&nbsp;&nbsp;</a>
            </span>
            </td>
        );
    }
});
