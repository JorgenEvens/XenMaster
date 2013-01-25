/*
 * VMMetrics.java
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
package org.xenmaster.api;

import java.util.*;
import org.xenmaster.controller.BadAPICallException;

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
