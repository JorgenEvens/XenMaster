/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.wgr.settings.Settings;
import net.wgr.utility.GlobalExecutorService;

/**
 * 
 * @created Oct 6, 2011
 * @author double-u
 */
public class MonitoringAgent implements Runnable {
    protected boolean lazy = false;
    protected Map<String, Record> vmData, hostData;
    protected Map<String, ParsedRecord> vmParsed, hostParsed;
    
    protected void schedule() {
        int interval = (int) Settings.getInstance().get("Monitoring.Interval");
        GlobalExecutorService.get().scheduleAtFixedRate(this, interval, interval, TimeUnit.SECONDS);
    }
    
    public List<Record> requestVMData(String ref, int start, int delta, int end) {
        return null;
    }
    
    public List<Record> requestHostData(String ref, int start, int delta, int end) {
        return null;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
