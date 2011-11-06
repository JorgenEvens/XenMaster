/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.pool;

import java.util.Set;
import net.wgr.xenmaster.entities.Host;

/**
 * 
 * @created Nov 2, 2011
 * @author double-u
 */
public class IllegalHostsException extends Exception {

    protected Set<Host> hosts;

    public IllegalHostsException(Set<Host> hosts) {
        this.hosts = hosts;
    }

    public Set<Host> getHosts() {
        return hosts;
    }

    @Override
    public String getMessage() {
        StringBuilder msg = new StringBuilder("Failed to connect to : ");
        for (Host h : hosts) {
            msg.append(h.getAddress().getCanonicalHostName()).append(", ");
        }
        return msg.toString();
    }
}
