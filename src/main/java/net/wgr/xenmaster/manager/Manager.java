/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.manager;

import java.util.HashMap;
import java.util.Map;
import net.wgr.xenmaster.connectivity.Connection;
import net.wgr.xenmaster.entities.Host;

/**
 * 
 * @created Oct 13, 2011
 * @author double-u
 */
public class Manager {
    protected HashMap<Host, Connection> connectedHosts;
    
    private static Manager instance;
    
    private Manager() {
        connectedHosts = new HashMap<>();
    }
    
    public static Manager get() {
        if (instance == null) instance = new Manager();
        return instance;
    }
    
    public void loadInConnections(Map<Host, Connection> conn) {
        this.connectedHosts.putAll(conn);
    }
}
