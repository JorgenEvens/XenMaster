/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import com.google.common.collect.ArrayListMultimap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.wgr.settings.Settings;
import net.wgr.utility.GlobalExecutorService;
import net.wgr.xenmaster.entities.Host;

/**
 * 
 * @created Oct 6, 2011
 * @author double-u
 */
public class MonitoringAgent implements Runnable {
    protected boolean lazy = false;
    protected ArrayListMultimap<String, Record> vmData, hostData;
    protected Map<String, ParsedRecord> vmParsed, hostParsed;
    
    public MonitoringAgent() {
        vmData = ArrayListMultimap.create();
        hostData = ArrayListMultimap.create();
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
