/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.manager;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import net.wgr.xenmaster.connectivity.Connection;
import net.wgr.xenmaster.entities.Host;
import net.wgr.xenmaster.manager.pool.Pool;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 13, 2011
 * @author double-u
 */
public class Manager {
    protected HashMap<Host, Connection> connectedHosts;
    protected Pool pool;
    
    private static Manager instance;
    
    private Manager() {
        connectedHosts = new HashMap<>();
        pool = new Pool();
    }
    
    public static Manager get() {
        if (instance == null) instance = new Manager();
        return instance;
    }
    
    public void loadInConnections(Map<Host, Connection> conn) {
        this.connectedHosts.putAll(conn);
    }
    
    public void boot() {
        try {
            pool.boot();
            if (pool.becomeMaster()) {
                
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass()).error("Pool init sequence failed", ex);
        }
        
    }
}
