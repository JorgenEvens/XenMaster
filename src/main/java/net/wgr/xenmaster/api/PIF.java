/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.net.InetAddress;
import java.util.List;
import net.wgr.xenmaster.controller.BadAPICallException;

/**
 * Physical (Network) Interface
 * @created Oct 5, 2011
 * @author double-u
 */
public class PIF extends XenApiEntity {

    protected int mtu;
    protected int vlan;
    protected String device;
    protected String mac;
    protected boolean attached, management, physical;
    protected String ip, netmask, gateway, dnsServer;

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
        return getAllEntities(PIF.class);
    }

    public String getMAC() {
        return mac;
    }

    public int getMTU() {
        return mtu;
    }

    public int getVLAN() {
        return vlan;
    }

    public String getDevice() {
        return device;
    }

    public String getIP() {
        return ip;
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
