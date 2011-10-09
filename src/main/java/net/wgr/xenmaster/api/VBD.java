/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @created Oct 10, 2011
 * @author double-u
 */
public class VBD extends XenApiEntity {
    
    protected String VM;
    protected String VDI;
    protected String deviceName;
    protected boolean bootable;
    protected Mode mode;
    protected Type type;
    protected boolean attached;
    protected int statusCode;
    protected String statusDetail;
    @Fill
    protected Map<String, String> runtimeProperties;
    protected String qosAlgorithm;
    @Fill
    protected Object[] supportedQosAlgorithms;
    protected String metrics;

    public VBD(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public VBD(String ref) {
        super(ref);
    }

    public VDI getVDI() {
        VDI = value(VDI, "get_VDI");
        return new VDI(VDI);
    }

    public VM getVM() {
        VM = value(VM, "get_VM");
        return new VM(VM);
    }

    public boolean isBootable() {
        return bootable;
    }

    public boolean isAttached() {
        return attached;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getMetrics() {
        return metrics;
    }

    public Mode getMode() {
        return mode;
    }

    public String getQosAlgorithm() {
        return qosAlgorithm;
    }

    public Map<String, String> getRuntimeProperties() {
        return runtimeProperties;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public Object[] getSupportedQosAlgorithms() {
        return supportedQosAlgorithms;
    }

    public Type getType() {
        return type;
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("attached", "currently_attached");
        map.put("deviceName", "device");
        return map;
    }    
    
    public static enum Mode {
        RO, RW
    }
    
    public static enum Type {
        CD, DISK
    }
}
