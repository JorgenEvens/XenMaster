/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wgr.xenmaster.controller.BadAPICallException;
import org.apache.log4j.Logger;

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
    @Fill
    protected Map<String, String> softwareVersion;
    @Fill
    protected Object[] hostCPUs;

    public Host(String ref) {
        super(ref);
    }

    public Host(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public void shutdown() throws BadAPICallException {
        dispatch("shutdown");
    }

    public void reboot() throws BadAPICallException {
        dispatch("reboot");
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

    public List<PCPU> getCPUs() throws BadAPICallException {
        hostCPUs = value(hostCPUs, "get_host_CPUs");
        ArrayList<PCPU> cpus = new ArrayList<>();
        for (Object cpu : hostCPUs) {
            cpus.add(new PCPU((String) cpu));
        }
        return cpus;
    }

    public PCPU getCPU(int number) throws BadAPICallException {
        hostCPUs = value(hostCPUs, "get_host_CPUs");
        if (number > hostCPUs.length - 1) {
            Logger.getLogger(getClass()).error("Tried to retrieve CPU which doesn't exist on the physical system");
        }
        return new PCPU((String) hostCPUs[number]);
    }

    public List<VM> getResidentVMs() throws BadAPICallException {
        Object[] refs = (Object[]) dispatch("get_resident_VMs");
        ArrayList<VM> vms = new ArrayList<>();
        for (Object obj : refs) {
            // Lame
            String ref = (String) obj;
            if (ref.equals("00000000-0000-0000-0000-000000000000")) {
                continue;
            }
            vms.add(new VM(ref, true));
        }
        return vms;
    }

    public List<PIF> getPhysicalInterfaces() throws BadAPICallException {
        Object[] refs = (Object[]) dispatch("get_PIFs");
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
        i.put("hostCPUs", "host_CPUs");
        return i;
    }
}
