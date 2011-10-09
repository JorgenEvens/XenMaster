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
public class VIF extends XenApiEntity {

    protected String deviceName;
    protected String network;
    protected String VM;
    protected String MAC;
    protected int MTU;
    protected boolean attached;
    protected int statusCode;
    protected String statusDetail;
    @Fill
    protected Map<String, String> runtimeProperties;
    protected String metrics;
    
    public VIF(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public VIF(String ref) {
        super(ref);
    }

    public String getMAC() {
        return MAC;
    }
    
    public void setMAC(String MAC) {
        this.MAC = setter(MAC, "set_MAC");
    }

    public int getMTU() {
        return MTU;
    }

    public String getVM() {
        return VM;
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

    public String getNetwork() {
        return network;
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

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceName", "device");
        map.put("attached", "currently_attached");
        return map;
    }
    
    
}
