/*
 * Host.java
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xenmaster.controller.BadAPICallException;
import org.xenmaster.controller.Controller;

/**
 * Xen host
 * @created Oct 2, 2011
 * @author double-u
 */
public class Host extends XenApiEntity {

    protected boolean enabled;
    protected String apiMajorVersion, apiMinorVersion;
    protected String apiVendor;
    protected String nameLabel, nameDescription;
    protected String scheduler;
    @Fill
    protected Map<String, String> softwareVersion, biosStrings, chipsetInfo, cpuInfo;
    @Fill
    protected Map<String, String> otherConfig;
    protected String address;
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

    public Map<String, String> getBiosStrings() {
        return biosStrings;
    }

    public Map<String, String> getChipsetInfo() {
        return chipsetInfo;
    }
    
    public boolean hasIOMMU() {
        return (getChipsetInfo().containsKey("iommu") && Boolean.parseBoolean(getChipsetInfo().get("iommu")));
    }

    public Map<String, String> getCpuInfo() {
        return cpuInfo;
    }
    
    public InetAddress getAddress() throws UnknownHostException {
        return InetAddress.getByName(address);
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

    public String getScheduler() {
        return scheduler;
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

    @Override
	protected Map<String, String> interpretation() {
        HashMap<String, String> i = new HashMap<>();
        i.put("majorApiVersion", "api_version_major");
        i.put("minorApiVersion", "api_version_minor");
        i.put("apiVendor", "api_version_vendor");
        i.put("schedulingPolicy", "sched_policy");
        i.put("hostCPUs", "host_CPUs");
        i.put("scheduler", "sched_policy");
        return i;
    }
}
