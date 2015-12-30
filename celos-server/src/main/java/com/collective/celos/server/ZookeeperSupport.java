package com.collective.celos.server;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.*;

import java.util.List;
import java.util.stream.Collectors;

public class ZookeeperSupport {

    private static final String INCREMENT_PREFIX = "n_";
    private static final String ROOT_NODE_NAME = "celos";
    private static final int RETRY_INTERVAL_MS = 1000;

    private final CuratorFramework client;

    ZookeeperSupport(CuratorFramework client) {
        this.client = client;
    }

    public ZookeeperSupport(String connectionString) throws Exception {
        RetryPolicy retryPolicy = new RetryForever(RETRY_INTERVAL_MS);
        this.client = CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
        client.start();
    }

    public CelosInstance createEphemeralNode() throws Exception {
        String serverEphemeralNode = client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(ROOT_NODE_NAME + "/" + INCREMENT_PREFIX);
        return new CelosInstance(serverEphemeralNode);
    }

    public List<CelosInstance> getCelosInstances() throws Exception {
        return client.getChildren().forPath(ROOT_NODE_NAME).stream()
                .map(offer -> new CelosInstance(ROOT_NODE_NAME + "/" + offer))
                .sorted(CelosInstance.ID_COMPARATOR).collect(Collectors.toList());
    }

    @Override
    protected void finalize() throws Throwable {
        client.close();
    }

}
