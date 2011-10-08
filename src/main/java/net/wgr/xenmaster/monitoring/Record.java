/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import java.util.ArrayList;
import java.util.Map;
import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.controller.Controller;
import net.wgr.xenmaster.entities.VM;
import net.wgr.xenmaster.entities.VMMetrics;

/**
 * 
 * @created Oct 6, 2011
 * @author double-u
 */
public class Record {

    protected float CPUusage;
    protected float CPUtotal;
    protected int memoryUsage;
    protected int memoryTotal;

    public Record(float CPUusage, float CPUtotal, int memoryUsage, int memoryTotal) {
        this.CPUtotal = CPUtotal;
        this.CPUusage = CPUusage;
        this.memoryTotal = memoryTotal;
        this.memoryUsage = memoryUsage;
    }

    public Record(String ref, boolean isVM) {
        try {
            if (isVM) {
                int totalMem = (int) Controller.dispatch("VM.get_memory_static_max", ref);
                VMMetrics vmr = new VM(ref).getMetrics();
                float total = 0;
                ArrayList<Float> percentages = new ArrayList<>();
                for (Map.Entry<Integer, Integer> entry : vmr.getPCPUs().entrySet()) {
                    
                }
            } else {
                
            }
        } catch (BadAPICallException ex) {
        }
    }
}
