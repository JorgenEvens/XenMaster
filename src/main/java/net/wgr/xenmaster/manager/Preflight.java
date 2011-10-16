/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.manager;

import java.util.HashMap;
import net.wgr.xenmaster.connectivity.Connection;
import net.wgr.xenmaster.entities.Host;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 13, 2011
 * @author double-u
 */
public class Preflight {
    
    public void connectToHosts() {
        Host h = new Host();
        HashMap<Host, Connection> conns = new HashMap<>();
        for (Host host : h.getAll()) {
            try {
                Connection c = h.connect();
                if (c != null) {
                    conns.put(h, c);
                } else {
                    if (h.getAddress().isReachable(500)) {
                        Logger.getLogger(getClass()).warn("Host " + h.getAddress().getCanonicalHostName() + " is up, but XAPI is not responding. Check your configuration.");
                    } else {
                        Logger.getLogger(getClass()).info("Failed to connect to " + h.getAddress().getCanonicalHostName());
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass()).error(ex);
            }
        }
        Manager.get().loadInConnections(conns);
    }
}
