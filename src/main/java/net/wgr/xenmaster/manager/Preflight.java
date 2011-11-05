/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.wgr.xenmaster.connectivity.Connection;
import net.wgr.xenmaster.entities.Host;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 13, 2011
 * @author double-u
 */
public class Preflight {
    
    public static Map<Host, Connection> checkHosts(Set<Host> hosts) {
        HashMap<Host, Connection> conns = new HashMap<>();
        for (Host host : hosts) {
            try {
                Connection c = host.connect();
                if (c != null) {
                    conns.put(host, c);
                } else {
                    if (host.getAddress().isReachable(500)) {
                        Logger.getLogger(Preflight.class).warn("Host " + host.getAddress().getCanonicalHostName() + " is up, but XAPI is not responding. Check your configuration.");
                    } else {
                        Logger.getLogger(Preflight.class).warn("Failed to connect to " + host.getAddress().getCanonicalHostName());
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(Preflight.class).error(ex);
            }
        }
        return conns;
    }
}
