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

import com.google.common.collect.ArrayListMultimap;
import com.lmax.disruptor.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.xenmaster.monitoring.data.Record;
import org.xenmaster.monitoring.engine.Collector;
import org.xenmaster.monitoring.engine.Correlator;
import org.xenmaster.monitoring.engine.Slot;

/**
 *
 * @created Oct 6, 2011
 *
 * @author double-u
 */
public class MonitoringAgent {

    protected boolean lazy = false, run;
    protected EventHandler eventHandler;
    protected Comptroller comptroller;
    protected Emitter emitter;
    protected Collector collector;
    protected Correlator correl;
    protected EventPublisher publisher;
    protected ArrayListMultimap<String, Record> vmData, hostData;
    protected ConcurrentSkipListMap<String, String> data;
    protected TimeInfo timeInfo;
    // Disrupting your pants
    protected RingBuffer<Record> ringBuffer;
    protected Executor engine;
    protected static final int RING_SIZE = 32;
    public static final String NTP_SERVER = "pool.ntp.org";
    private static MonitoringAgent instance;

    private MonitoringAgent() {
        vmData = ArrayListMultimap.create();
        hostData = ArrayListMultimap.create();
        data = new ConcurrentSkipListMap<>();
        NTPUDPClient nuc = new NTPUDPClient();
        ringBuffer = new RingBuffer<>(Record.EVENT_FACTORY, new SingleThreadedClaimStrategy(RING_SIZE), new BlockingWaitStrategy());
        eventHandler = new EventHandler();
        comptroller = new Comptroller();
        emitter = new Emitter();
        collector = new Collector();
        correl = new Correlator();

        setUpEngine();

        try {
            timeInfo = nuc.getTime(InetAddress.getByName(NTP_SERVER));
            timeInfo.computeDetails();
            Logger.getLogger(getClass()).info("Current time " + new DateTime(System.currentTimeMillis() + timeInfo.getOffset()).toString("dd/MM/yyyy HH:mm:ss.S")
                    + ". Clock drift " + timeInfo.getOffset() + " milliseconds");
        }
        catch (IOException ex) {
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
        engine = Executors.newCachedThreadPool(new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("Monitoring engine " + r.getClass().getSimpleName());
                return t;
            }
        });

        SequenceBarrier collectorBarrier = ringBuffer.newBarrier();
        BatchEventProcessor<Record> cr = new BatchEventProcessor<>(ringBuffer, collectorBarrier, collector);

        SequenceBarrier correlatorBarrier = ringBuffer.newBarrier(cr.getSequence());
        BatchEventProcessor<Record> cb = new BatchEventProcessor<>(ringBuffer, correlatorBarrier, correl);

        ringBuffer.setGatingSequences(cb.getSequence());

        engine.execute(cr);
        engine.execute(cb);
    }

    public void boot() {
        collector.boot();
    }

    public Correlator getCorrelator() {
        return correl;
    }

    protected class EventPublisher extends Collector.TimingProvider {

        @Override
        public void run() {
            while (run) {
                long sequence = ringBuffer.next();
                Slot nextSlot = getNextSlot();
                if (nextSlot == null) {
                    run = false;
                    break;
                }
                
                Record r = ringBuffer.get(sequence);
                r.attachSlot(nextSlot);
                ringBuffer.publish(sequence);
            }
        }
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
        engine.execute(new EventPublisher());
    }

    public void stop() {
        run = false;
        eventHandler.stop();
        comptroller.stop();
    }
}
