/*
 * PIF.java
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
package org.xenmaster.api.entities;

import java.net.InetAddress;
import java.util.List;
import org.xenmaster.controller.BadAPICallException;

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
    protected Mode mode;
    protected boolean attached, management, physical;
    protected String ip, netmask, gateway, dnsServer;
    
    public PIF() {
        
    }

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
    
    public Mode getMode() {
        return mode;
    }

    public static enum Mode {

        NONE, DHCP, STATIC
    }
}
