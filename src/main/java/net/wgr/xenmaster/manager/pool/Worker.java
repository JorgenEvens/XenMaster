/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.manager.pool;

import java.net.InetAddress;

/**
 * Just a friendly name for Slave
 * @created Oct 23, 2011
 * @author double-u
 */
public class Worker {
    protected long lastSeen;
    protected InetAddress address;
    protected boolean master;
    
    public void seen() {
        lastSeen = System.currentTimeMillis();
    }
    
    public boolean isMaster() {
        return master;
    }
    
    public long getLastSeen() {
        return lastSeen;
    }
}
