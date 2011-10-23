/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wgr.xenmaster.controller.BadAPICallException;

/**
 * 
 * @created Oct 6, 2011
 * @author double-u
 */
public class VMMetrics extends XenApiEntity {

    protected int actualMemory;
    protected int VCPUs;
    @Fill
    protected Map<Integer, Double> VCPUutilisation;
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
    protected String getAPIName() {
        return "VM_metrics";
    }
   
    public List<PCPU> getPCPUs(Host physical) throws BadAPICallException {
        this.PCPUs = getPCPUs();
        ArrayList<PCPU> cpus = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : PCPUs.entrySet()) {
            cpus.add(physical.getCPU(entry.getValue().intValue()));
        }

        return cpus;
    }

    public Map<Integer, Integer> getPCPUs() {
        return value(PCPUs, "get_VCPUs_CPU");
    }

    public int getVCPUs() {
        return VCPUs;
    }

    public Map<Integer, Double> getVCPUutilisation() {
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

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> m = new HashMap<>();
        m.put("actualMemory", "memory_actual");
        m.put("VCPUs", "VCPUs_number");
        m.put("VCPUutilisation", "VCPUs_utilisation");
        m.put("PCPUs", "VCPUs_CPU");
        return m;
    }
}
