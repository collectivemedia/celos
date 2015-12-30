package com.collective.celos.server;

import com.collective.celos.CelosClient;
import com.collective.celos.Constants;

import java.net.URI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CelosIterator {

    private final int autoScheduleSec;
    private final Timer timer;
    private final CelosClient celosClient;
    private final ZookeeperSupport zookeeperSupport;
    private final TimerTask timerTask;
    private final CelosInstance celosInstance;

    public CelosIterator(int port, int autoSchedule, ZookeeperSupport zookeeperSupport) throws Exception {
        this.autoScheduleSec = autoSchedule;
        this.timer = new Timer(true);
        this.celosClient = new CelosClient(URI.create("http://localhost:" + port));
        this.zookeeperSupport = zookeeperSupport;
        if (zookeeperSupport != null) {
            this.celosInstance = zookeeperSupport.createEphemeralNode();
            this.timerTask = createTimerTaskZookeeper();
        } else {
            this.celosInstance = null;
            this.timerTask = createTimerTaskRegular();
        }
    }


    public void start() throws Exception {
        timer.schedule(timerTask, 0, autoScheduleSec * Constants.SECOND_MS);
    }

    public void stop() {
        timer.cancel();
    }

    void iterateRespectingZookeeper() throws Exception {
        List<CelosInstance> celosInstances = zookeeperSupport.getCelosInstances();
        int index = celosInstances.indexOf(celosInstance);
        celosClient.iterateScheduler(index, celosInstances.size());
    }

    private TimerTask createTimerTaskZookeeper() {

        return new TimerTask() {
            @Override
            public void run() {
                try {
                    iterateRespectingZookeeper();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private TimerTask createTimerTaskRegular() {

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
