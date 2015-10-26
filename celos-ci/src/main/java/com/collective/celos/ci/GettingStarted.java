package com.collective.celos.ci;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;

public class GettingStarted {


    public static void main(String[] args) {

        Cluster cluster;
        Session session;
        ResultSet results;
        Row rows;

        // Connect to the cluster and keyspace "demo"
        cluster = Cluster
                .builder()
                .addContactPoint("localhost")
                .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
                .withLoadBalancingPolicy(
                        new TokenAwarePolicy(new DCAwareRoundRobinPolicy()))
                .build();
        session = cluster.connect("demo");

        PreparedStatement statement = session.prepare("INSERT INTO users (id, data) VALUES (?, ?)");
        BoundStatement boundStatement = new BoundStatement(statement);

        for (int i=0; i<4000; i++) {
            session.execute(boundStatement.bind(i, "data_" + i));
        }

        // Clean up the connection by closing it
        cluster.close();
    }
}
