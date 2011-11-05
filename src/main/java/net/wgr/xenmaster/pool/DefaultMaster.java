/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.pool;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.wgr.core.data.Retrieval;
import net.wgr.xenmaster.entities.Host;

/**
 * 
 * @created Nov 2, 2011
 * @author double-u
 */
public class DefaultMaster implements Master {
    
    protected HashSet<Host> hosts;
    
    protected void planWork() {
        // Retrieve host that are to be managed
        for (Host h : Retrieval.getRowsAs(Host.class, Retrieval.getAllRowsFromColumnFamily("xen-hosts")).values()) {
            if (h.isActive()) hosts.add(h);
        }
        // Distribute
        int tasksPerWorker = (int) Math.ceil(hosts.size() / Pool.get().getWorkers().size());
        int currentWorker = 0;
        for (int i = 0; i < hosts.size(); i++) {
            
        }
    }
    
    // Called when a configuration change has been made
    public void reorganize() {
        planWork();
    }

    @Override
    public List<Worker> getWorkers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Worker> List<T> getWorkers(Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void workerReplacedByFriend(Friend friend, Worker worker) throws WorkerIsAliveException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Host> getManagedEntities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadInHosts(Set<Host> hosts) throws IllegalHostsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public InetAddress getAddress() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
