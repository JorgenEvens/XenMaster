/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.controller.Controller;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 9, 2011
 * @author double-u
 */
public class GuestMetrics extends XenApiEntity {

    protected Map<String, String> osVersion, PVdriversVersion, memory, disks, networks, other;
    protected Date lastUpdate;

    public GuestMetrics(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public GuestMetrics(String ref) {
        super(ref);
    }

    @Override
    protected String getAPIName() {
        return "VM_guest_metrics";
    }
    
    public static List<GuestMetrics> getAll() {
        try {
            List<String> gms = (List<String>) Controller.dispatch("VM_guest_metrics.get_all");
            for (String gmr : gms){
                
            }
        } catch (BadAPICallException ex) {
            Logger.getLogger(GuestMetrics.class).error(ex);
        }
        return null;
    }

    public Map<String, String> getPVdriversVersion() {
        return PVdriversVersion;
    }

    public Map<String, String> getDisks() {
        return disks;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Map<String, String> getMemory() {
        return memory;
    }

    public Map<String, String> getNetworks() {
        return networks;
    }

    public Map<String, String> getOsVersion() {
        return osVersion;
    }

    public Map<String, String> getOther() {
        return other;
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("PVdriversVersion", "PV_drivers_version");
        return map;
    }
}
