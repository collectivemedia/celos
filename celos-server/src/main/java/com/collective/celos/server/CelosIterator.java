package com.collective.celos.server;

import com.collective.celos.CelosClient;
import com.collective.celos.Constants;

import java.net.URI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CelosIterator {

    private final int autoSchedule;
    private final Timer timer;
    private final CelosClient celosClient;

    public CelosIterator(int port, int autoSchedule) throws Exception {
        this.autoSchedule = autoSchedule;
        this.timer = new Timer(true);
        this.celosClient = new CelosClient(URI.create("http://localhost:" + port));
    }

    public void start(ZookeeperSupport zookeeperSupport) throws Exception {
        CelosInstance ephemeralNode = zookeeperSupport.createEphemeralNode();
        timer.schedule(createTimerZookeeper(ephemeralNode, zookeeperSupport), 0, autoSchedule * Constants.SECOND_MS);
    }

    public void start() throws Exception {
        timer.schedule(createTimerTask(), 0, autoSchedule * Constants.SECOND_MS);
    }

    public void stop() {
        timer.cancel();
    }

    private TimerTask createTimerZookeeper(CelosInstance celosInstance, ZookeeperSupport zookeeperSupport) {

        return new TimerTask() {
            @Override
            public void run() {
                try {
                    List<CelosInstance> celosInstances = zookeeperSupport.getCelosInstances();
                    int index = celosInstances.indexOf(celosInstance);
                    celosClient.iterateScheduler(index, celosInstances.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private TimerTask createTimerTask() {

        return new TimerTask() {
            @Override
            public void run() {
                try {
                    celosClient.iterateScheduler();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
