/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import com.google.common.collect.ArrayListMultimap;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import net.wgr.settings.Settings;
import net.wgr.utility.GlobalExecutorService;
import net.wgr.xenmaster.entities.Host;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

/**
 * 
 * @created Oct 6, 2011
 * @author double-u
 */
public class MonitoringAgent implements Runnable {

    protected boolean lazy = false, run;
    protected EventHandler eventHandler;
    protected Comptroller comptroller;
    protected Emitter emitter;
    protected ArrayListMultimap<String, Record> vmData, hostData;
    protected Map<String, ParsedRecord> vmParsed, hostParsed;
    protected ConcurrentSkipListMap<String, String> data;
    protected TimeInfo timeInfo;
    // Disrupting your pants
    protected RingBuffer<Record> ringBuffer;
    protected SequenceBarrier barrier;
    protected static final int RING_SIZE = 256;
    private static MonitoringAgent instance;

    private MonitoringAgent() {
        vmData = ArrayListMultimap.create();
        hostData = ArrayListMultimap.create();
        data = new ConcurrentSkipListMap<>();
        NTPUDPClient nuc = new NTPUDPClient();
        ringBuffer = new RingBuffer<>(Record.EVENT_FACTORY, new SingleThreadedClaimStrategy(RING_SIZE), new SleepingWaitStrategy());
        barrier = ringBuffer.newBarrier();
        eventHandler = new EventHandler();
        comptroller = new Comptroller();
        emitter = new Emitter();

        try {
            timeInfo = nuc.getTime(InetAddress.getByName("pool.ntp.org"));
            timeInfo.computeDetails();
            Logger.getLogger(getClass()).info("It is now " + new DateTime(System.currentTimeMillis() + timeInfo.getOffset()).toString("dd/MM/yyyy HH:mm:ss.S") + ". Your host's clock is drifting by " + timeInfo.getOffset() + " milliseconds");
        } catch (IOException ex) {
            Logger.getLogger(getClass()).warn("NTP time retrieval failed", ex);
        }
    }

    public static MonitoringAgent instance() {
        if (instance == null) {
            instance = new MonitoringAgent();
        }
        return instance;
    }

    public void boot() {
        schedule();
    }

    protected void schedule() {
        int interval = (int) ((double) Settings.getInstance().get("Monitoring.Interval") * 1000);
        GlobalExecutorService.get().scheduleAtFixedRate(this, interval, interval, TimeUnit.MILLISECONDS);
    }

    public List<Record> requestVMData(String ref, int start, int delta, int end) {
        return null;
    }

    public List<Record> requestHostData(String ref, int start, int delta, int end) {
        return null;
    }

    @Override
    public void run() {
        for (Host host : Host.getAll()) {
            String ref = host.getId().toString();
            Record r = new Record(ref, true);
            hostData.put(ref, r);
        }
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public Comptroller getComptroller() {
        return comptroller;
    }

    public void start() {
        run = true;
        emitter.listenToEvents(eventHandler);
        eventHandler.start();
        comptroller.scheduleSensors();
    }

    public void stop() {
        run = false;
        eventHandler.stop();
        comptroller.stop();
    }
}
