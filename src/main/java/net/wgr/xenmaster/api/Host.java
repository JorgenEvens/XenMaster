/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.controller.Controller;
import org.apache.log4j.Logger;

/**
 * Xen host
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

    public Host() {
    }

    public Host(String ref) {
        super(ref);
    }

    public Host(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public void shutdown() throws BadAPICallException {
        if (enabled) {
            disable();
        }
        
        // todo : If this is a host we're connected to, do something smart and break up the connection
        
        dispatch("shutdown");
    }

    public void disable() throws BadAPICallException {
        dispatch("disable");
    }
    
    public void enable() throws BadAPICallException {
        dispatch("enable");
    }

    public void reboot() throws BadAPICallException {
        if (enabled) {
            disable();
        }
        dispatch("reboot");
    }

    public void scanPIFs() throws BadAPICallException {
        Controller.dispatch("PIF.scan", this.getReference());
    }

    public PIF createPIFWithInterface(String interfaceName, String macAddress) throws BadAPICallException {
        String ref = (String) Controller.dispatch("PIF.introduce", this.getReference(), macAddress, interfaceName);
        return new PIF(ref);
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
        return getEntities(PCPU.class, "get_host_CPUs");
    }

    public PCPU getCPU(int number) throws BadAPICallException {
        hostCPUs = value(hostCPUs, "get_host_CPUs");
        if (number > hostCPUs.length - 1) {
            Logger.getLogger(getClass()).error("Tried to retrieve CPU which doesn't exist on the physical system");
        }
        return new PCPU((String) hostCPUs[number]);
    }

    public String callPlugin(String pluginName, String methodName, Map<String, String> args) throws BadAPICallException {
        return (String) dispatch("call_plugin", pluginName, methodName, args);
    }

    public static List<Host> getAll() throws BadAPICallException {
        return getAllEntities(Host.class);
    }

    public List<VM> getResidentVMs() throws BadAPICallException {
        return getEntities(VM.class, "get_resident_VMs");
    }

    public List<PIF> getPhysicalInterfaces() throws BadAPICallException {
        return getEntities(PIF.class, "get_PIFs");
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
