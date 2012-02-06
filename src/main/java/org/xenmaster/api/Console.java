/*
 * Console.java
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

import java.util.List;
import java.util.Map;

import org.xenmaster.controller.BadAPICallException;

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
