/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @created Oct 6, 2011
 * @author double-u
 */
public class VMMetrics extends XenApiEntity {
    protected int actualMemory;
    protected int VCPUs;
    @Fill
    protected Map<Integer, Float> VCPUutilisation;
    @Fill
    protected Map<Integer, Integer> PCPUs;
    protected Date startTime, lastUpdated;

    public VMMetrics(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public VMMetrics(String ref) {
        super(ref);
    } 

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> m = new HashMap<>();
        m.put("actualMemory", "memory_actual");
        m.put("VCPUs", "VCPUs_number");
        m.put("VCPUutilisation", "VCPUs_utilisation");
        m.put("PCPUs", "VCPUs_CPU");
        return m;
    }

    public Map<Integer, Integer> getPCPUs() {
        return PCPUs;
    }

    public int getVCPUs() {
        return VCPUs;
    }

    public Map<Integer, Float> getVCPUutilisation() {
        return VCPUutilisation;
    }

    public int getActualMemory() {
        return actualMemory;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public Date getStartTime() {
        return startTime;
    }
}
