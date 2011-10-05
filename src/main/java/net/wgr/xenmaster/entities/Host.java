/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wgr.xenmaster.controller.Controller;

/**
 * 
 * @created Oct 2, 2011
 * @author double-u
 */
public class Host extends XenApiEntity {
    
    protected boolean enabled;
    
    protected String apiMajorVersion, apiMinorVersion;
    protected String apiVendor;
    protected String nameLabel, schedulingPolicy, nameDescription;

    public Host(String ref) {
        super(ref);
    }

    public Host(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public String getMajorApiVersion() {
        return apiMajorVersion;
    }

    public String getMinorApiVersion() {
        return apiMinorVersion;
    }

    public String getApiVendor() {
        return apiVendor;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getNameDescription() {
        return nameDescription;
    }

    public String getNameLabel() {
        return nameLabel;
    }

    public String getSchedulingPolicy() {
        return schedulingPolicy;
    }
    
    public List<VM> getResidentVMs() {
        Object[] refs = (Object[]) Controller.dispatch("host.get_resident_VMs", this.reference);
        ArrayList<VM> vms = new ArrayList<>();
        for (Object obj : refs) {
            // Lame
            String ref = (String) obj;
            if (ref.equals("00000000-0000-0000-0000-000000000000")) continue;
            vms.add(new VM(ref, true));
        }
        return vms;
    }
    
    public List<PIF> getPhysicalInterfaces() {
        Object[] refs = (Object[]) Controller.dispatch("host.get_PIFs", this.reference);
        ArrayList<PIF> pifs = new ArrayList<>();
        for (Object obj : refs) {
            String ref = (String) obj;
            pifs.add(new PIF(ref, true));
        }
        return pifs;
    }
    
    protected Map<String, String> interpretation() {
        HashMap<String, String> i = new HashMap<>();
        i.put("majorApiVersion", "api_version_major");
        i.put("minorApiVersion", "api_version_minor");
        i.put("apiVendor", "api_version_vendor");
        i.put("schedulingPolicy", "sched_policy");
        return i;
    }
   
}
