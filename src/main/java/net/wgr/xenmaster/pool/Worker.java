/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.pool;

import java.net.InetAddress;
import java.util.Set;
import net.wgr.rmi.InvocationTarget;
import net.wgr.xenmaster.entities.Host;

/**
 * 
 * @created Nov 1, 2011
 * @author double-u
 */
public interface Worker extends InvocationTarget {
    /**
     * Retrieves entities managed and monitored by this worker
     * @return 
     */
    public Set<Host> getManagedEntities();
    
    /**
     * Hand off specified hosts to this worker
     * @param hosts 
     */
    public void loadInHosts(Set<Host> hosts) throws IllegalHostsException;
    
    public InetAddress getAddress();
    
    
    //todo
    //public void getMonitoringData(Request r);
}
