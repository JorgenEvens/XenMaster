/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.wgr.xenmaster.controller.BadAPICallException;

/**
 * 
 * @created Oct 10, 2011
 * @author double-u
 */
public class VIF extends XenApiEntity {

    @ConstructorArgument
    protected String deviceName;
    @ConstructorArgument
    protected String network;
    @ConstructorArgument
    protected String VM;
    @ConstructorArgument
    protected String MAC;
    @ConstructorArgument
    protected int MTU;
    protected boolean attached, autogeneratedMAC;
    protected int statusCode;
    protected String statusDetail;
    @Fill
    @ConstructorArgument
    protected Map<String, String> runtimeProperties, otherConfig;
    protected String metrics;
    protected final static String XEN_MAC_ADDRESS_PREFIX = "00:16:3e";

    public VIF() {
    }

    public VIF(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public VIF(String ref) {
        super(ref);
    }

    public void plug() throws BadAPICallException {
        dispatch("plug");
    }

    public void unplug() throws BadAPICallException {
        dispatch("unplug");
    }

    public String create(VM vm, String deviceName, Network network) throws BadAPICallException {
        this.VM = vm.getReference();
        this.deviceName = deviceName;
        this.network = network.getReference();
        if (this.MAC == null || this.MAC.isEmpty()) this.MAC = generateMACAddress();
        if (this.MTU == 0) this.MTU = network.getMTU();

        this.reference = (String) dispatch("create", collectConstructorArgs());
        return this.reference;
    }

    public void destroy() throws BadAPICallException {
        dispatch("destroy");
    }

    public String getMAC() {
        return MAC;
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

    public boolean hasAutogeneratedMAC() {
        return autogeneratedMAC;
    }

    public String generateMACAddress() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(XEN_MAC_ADDRESS_PREFIX);
        while (sb.length() < 17) {
            if ((sb.length() + 1) % 3 == 0) {
                sb.append(':');
            }
            sb.append(Integer.toHexString(random.nextInt(16)));
        }
        return sb.toString();
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceName", "device");
        map.put("attached", "currently_attached");
        map.put("autogeneratedMAC", "MAC_autogenerated");
        return map;
    }
}
