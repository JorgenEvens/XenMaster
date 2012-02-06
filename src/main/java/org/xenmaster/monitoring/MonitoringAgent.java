/*
 * MonitoringAgent.java
 * Copyright (C) 2011,2012 Wannes De Smet
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.xenmaster.monitoring;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import net.wgr.settings.Settings;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.xenmaster.monitoring.data.Record;
import org.xenmaster.monitoring.engine.Slot;

import com.google.common.collect.ArrayListMultimap;
import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import org.xenmaster.monitoring.data.Parser;
import org.xenmaster.monitoring.engine.Collector;

/**
 * 
 * @created Oct 6, 2011
 * @author double-u
 */
public class MonitoringAgent {

    protected boolean lazy = false, run;
    protected EventHandler eventHandler;
    protected Comptroller comptroller;
    protected Emitter emitter;
    protected Collector collector;
    protected EventPublisher publisher;
    protected ArrayListMultimap<String, Record> vmData, hostData;
    protected ConcurrentSkipListMap<String, String> data;
    protected TimeInfo timeInfo;
    // Disrupting your pants
    protected RingBuffer<Record> ringBuffer;
    protected SequenceBarrier barrier;
    protected static final int RING_SIZE = 256;
    public static final String NTP_SERVER = "pool.ntp.org";
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
        collector = new Collector();
        
        setUpEngine();

        try {
            timeInfo = nuc.getTime(InetAddress.getByName(NTP_SERVER));
            timeInfo.computeDetails();
            Logger.getLogger(getClass()).info("It is now " + new DateTime(System.currentTimeMillis() + timeInfo.getOffset()).toString("dd/MM/yyyy HH:mm:ss.S")
                    + ". Your host's clock is drifting by " + timeInfo.getOffset() + " milliseconds");
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

    protected final void setUpEngine() {
        BatchEventProcessor<Record> cr = new BatchEventProcessor<>(ringBuffer, barrier, collector);
        BatchEventProcessor<Record> parser = new BatchEventProcessor<>(ringBuffer, barrier, new Parser());

        ringBuffer.setGatingSequences(cr.getSequence(), parser.getSequence());
    }

    public void boot() {
        schedule();
        collector.boot();
    }

    protected class EventPublisher extends Collector.SlotHandler {

        @Override
        public void run() {
            while (run) {
                long sequence = ringBuffer.next();
                Slot nextSlot = getNextSlot();
                Record r = ringBuffer.get(sequence);
                r.attachSlot(nextSlot);
                ringBuffer.publish(sequence);
            }
        }
    }

    protected void schedule() {
        int interval = (int) ((double) Settings.getInstance().get("Monitoring.Interval") * 1000);
    }

    public List<Record> requestVMData(String ref, int start, int delta, int end) {
        return null;
    }

    public List<Record> requestHostData(String ref, int start, int delta, int end) {
        return null;
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
        
        Thread ep = new Thread(new EventPublisher(), "Event publisher");
        ep.start();
    }

    public void stop() {
        run = false;
        eventHandler.stop();
        comptroller.stop();
    }
}
