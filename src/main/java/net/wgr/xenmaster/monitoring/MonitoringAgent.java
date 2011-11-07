/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import com.google.common.collect.ArrayListMultimap;
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

/**
 * 
 * @created Oct 6, 2011
 * @author double-u
 */
public class MonitoringAgent implements Runnable {
    protected boolean lazy = false;
    protected ArrayListMultimap<String, Record> vmData, hostData;
    protected Map<String, ParsedRecord> vmParsed, hostParsed;
    protected ConcurrentSkipListMap<String, String> data;
    protected TimeInfo timeInfo;
    
    public MonitoringAgent() {
        vmData = ArrayListMultimap.create();
        hostData = ArrayListMultimap.create();
        data = new ConcurrentSkipListMap<>();
        NTPUDPClient nuc = new NTPUDPClient();
        try {
            this.timeInfo = nuc.getTime(InetAddress.getByName("pool.ntp.org"));
            timeInfo.computeDetails();
            Logger.getLogger(getClass()).info("It is now " + (System.currentTimeMillis() + timeInfo.getOffset()) + ". Your host has an offset of " + (timeInfo.getOffset() / 1000) + " seconds");
        } catch (IOException ex) {
            Logger.getLogger(getClass()).warn("NTP time retrieval failed", ex);
        }
    }
    
    protected void schedule() {
        int interval = (int)((double) Settings.getInstance().get("Monitoring.Interval") * 1000);
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
        Host h = new Host();
        
        for (Host host : h.getAll()) {
            String ref = host.getId().toString();
            Record r = new Record(ref, true);
            hostData.put(ref, r);
        }
    }
    
}
