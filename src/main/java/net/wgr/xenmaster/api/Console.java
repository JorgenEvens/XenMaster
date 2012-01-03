/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.List;
import java.util.Map;
import net.wgr.xenmaster.controller.BadAPICallException;

/**
 * 
 * @created Dec 15, 2011
 * @author double-u
 */
public class Console extends XenApiEntity {
    
    protected String location;
    protected String vm;
    protected Protocol protocol;
    protected int port;
    @Fill
    protected Map<String, String> otherConfig;
    
    public Console() {
        
    }

    public Console(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public Console(String ref) {
        super(ref);
    }

    public String getLocation() {
        return location;
    }
    
    public VM getVM() {
        this.vm = value(vm, "get_vm");
        return new VM(vm, false);
    }

    public Map<String, String> getOtherConfig() {
        return otherConfig;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public int getPort() {
        return value(port, "get_port");
    }

    public void setPort(int port) throws BadAPICallException {
        this.port = setter(port, "set_port");
    }
    
    public static List<Console> getAll() throws BadAPICallException {
        return getAllEntities(Console.class);
    }
    
    public static enum Protocol {
        /**
         * The value does not belong to this enumeration
         */
        UNRECOGNIZED,
        /**
         * VT100 terminal
         */
        VT100,
        /**
         * Remote FrameBuffer protocol (as used in VNC)
         */
        RFB,
        /**
         * Remote Desktop Protocol
         */
        RDP
    }
}
