/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.controller.Controller;

/**
 * Physical (Network) Interface
 * @created Oct 5, 2011
 * @author double-u
 */
public class PIF extends XenApiEntity {

    protected int MTU;
    protected int VLAN;
    protected String device;
    protected String MAC;
    protected boolean attached, management, physical;
    protected String IP, netmask, gateway, dnsServer;

    public PIF(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public PIF(String ref) {
        super(ref);
    }

    public void destroy() throws BadAPICallException {
        dispatch("destroy");
    }

    public void unplug() throws BadAPICallException {
        dispatch("unplug");
    }

    public void plug() throws BadAPICallException {
        dispatch("plug");
    }

    public void reconfigureIP(Mode mode, InetAddress addr, String netmask, InetAddress gateway, InetAddress dnsServer) throws BadAPICallException {
        dispatch("reconfigure_ip", mode, addr.getHostAddress(), netmask, gateway.getHostAddress(), dnsServer.getHostAddress());
    }

    public static List<PIF> getAll() throws BadAPICallException {
        Map<String, Object> records = (Map) Controller.dispatch("PIF.get_all_records");
        ArrayList<PIF> objects = new ArrayList<>();
        for (Map.Entry<String, Object> entry : records.entrySet()) {
            PIF pif = new PIF(entry.getKey(), false);
            pif.fillOut((Map) entry.getValue());
            objects.add(pif);
        }
        return objects;
    }

    public String getMAC() {
        return MAC;
    }

    public int getMTU() {
        return MTU;
    }

    public int getVLAN() {
        return VLAN;
    }

    public String getDevice() {
        return device;
    }

    public String getIP() {
        return IP;
    }

    public boolean isAttached() {
        return attached;
    }

    public String getDnsServer() {
        return dnsServer;
    }

    public String getGateway() {
        return gateway;
    }

    public boolean isManagementInterface() {
        return management;
    }

    public String getNetmask() {
        return netmask;
    }

    public boolean isPhysical() {
        return physical;
    }

    public static enum Mode {

        NONE, DHCP, STATIC
    }
}
