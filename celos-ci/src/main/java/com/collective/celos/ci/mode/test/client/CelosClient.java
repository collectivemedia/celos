package com.collective.celos.ci.mode.test.client;

import com.collective.celos.ScheduledTime;
import com.collective.celos.ScheduledTimeFormatter;
import com.collective.celos.WorkflowID;
import com.google.common.collect.Maps;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by akonopko on 10.12.14.
 */
public class CelosClient {

    private final HttpClient client;
    private final ScheduledTimeFormatter timeFormatter;
    private final String address;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpGet workflowListGet;

    public CelosClient(String address) {
        this.address = address;
        this.client = new DefaultHttpClient();
        this.timeFormatter = new ScheduledTimeFormatter();
        this.workflowListGet = new HttpGet(address + "/workflow-list");
    }

    public List<WorkflowID> getWorkflowList() throws IOException {
        HttpResponse getResponse = client.execute(workflowListGet);
        InputStream content = getResponse.getEntity().getContent();
        return parseWorkflowIdsList(content);
    }

    public Map<ScheduledTime, ExternalWorkflowStatus> getWorkflowStatus(WorkflowID workflowID) throws IOException {
        return getWorkflowStatus(workflowID, null);
    }

    public Map<ScheduledTime, ExternalWorkflowStatus> getWorkflowStatus(WorkflowID workflowID, ScheduledTime scheduledTime) throws IOException {
        String timeQuery = "";
        if (scheduledTime != null) {
            timeQuery = "&time=" + getTimePrecise(scheduledTime);
        }
        HttpGet workflowListGet = new HttpGet(address + "/workflow?id=" + workflowID + timeQuery);
        HttpResponse getResponse = client.execute(workflowListGet);
        InputStream content = getResponse.getEntity().getContent();

        return parseWorkflowStatusesMap(content);
    }

    public void iterateScheduler(ScheduledTime schedTime) throws IOException {
        String schedTimeStr = getTimeInclusiveRange(schedTime);
        HttpPost post = new HttpPost(address + "/scheduler?time=" + schedTimeStr);
        HttpResponse postResponse = client.execute(post);
        EntityUtils.consume(postResponse.getEntity());
    }

    private static class WorkflowList {
        private List<WorkflowID> ids;

        private WorkflowList() {
        }

        public List<WorkflowID> getIds() {
            return ids;
        }

        public void setIds(List<WorkflowID> ids) {
            this.ids = ids;
        }
    }

    List<WorkflowID> parseWorkflowIdsList(InputStream content) throws IOException {
        return objectMapper.readValue(content, WorkflowList.class).getIds();
    }

    Map<ScheduledTime, ExternalWorkflowStatus> parseWorkflowStatusesMap(InputStream content) throws IOException {
        JsonNode responseTree = objectMapper.readTree(content);
        Iterator<Map.Entry<String, JsonNode>> iterator = responseTree.getFields();
        Map<ScheduledTime, ExternalWorkflowStatus> result = Maps.newHashMap();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            ScheduledTime itemSchedTime = new ScheduledTime(entry.getKey());
            ExternalWorkflowStatus workflowStatus = objectMapper.readValue(entry.getValue(), ExternalWorkflowStatus.class);
            result.put(itemSchedTime, workflowStatus);
        }
        return result;
    }

    private String getTimeInclusiveRange(ScheduledTime schedTime) {
        //add one second to include last slot in the time period
        return timeFormatter.formatPretty(schedTime.plusSeconds(1));
    }

    private String getTimePrecise(ScheduledTime schedTime) {
        return timeFormatter.formatPretty(schedTime);
    }

}
