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
    protected int vcpus;
    @Fill
    protected Map<Integer, Double> vcpuUtilisation;
    @Fill
    protected Map<Integer, Integer> pcpus;
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
        this.pcpus = getPCPUs();
        ArrayList<PCPU> cpus = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : pcpus.entrySet()) {
            cpus.add(physical.getCPU(entry.getValue().intValue()));
        }

        return cpus;
    }

    public Map<Integer, Integer> getPCPUs() {
        return value(pcpus, "get_VCPUs_CPU");
    }

    public int getVCPUs() {
        return vcpus;
    }

    public Map<Integer, Double> getVCPUutilisation() {
        return vcpuUtilisation;
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
        m.put("vcpus", "VCPUs_number");
        m.put("vcpuUtilisation", "VCPUs_utilisation");
        m.put("pcpus", "VCPUs_CPU");
        return m;
    }
}
