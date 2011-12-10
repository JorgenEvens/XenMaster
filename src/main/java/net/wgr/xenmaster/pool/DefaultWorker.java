/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.pool;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.wgr.xenmaster.connectivity.Connections;
import net.wgr.xenmaster.entities.Host;
import net.wgr.xenmaster.manager.Preflight;

/**
 * A worker is not a slave, mind you. A worker has his own responsibilities -- at least that's Masters led them to believe
 * @created Oct 23, 2011
 * @author double-u
 */
public class DefaultWorker implements Friend {
    protected long lastSeen;
    protected InetAddress address;
    protected boolean master;
    protected HashMap<Host, Connections> connections;
    protected Set<Friend> friends;

    public DefaultWorker() {
        connections = new HashMap<>();
    }
    
    public void seen() {
        lastSeen = System.currentTimeMillis();
    }
    
    public long getLastSeen() {
        return lastSeen;
    }

    @Override
    public Set<Host> getManagedEntities() {
        return connections.keySet();
    }

    @Override
    public void loadInHosts(Set<Host> hosts) throws IllegalHostsException {
        // Always use a rubber
        Map<Host, Connections> conns = Preflight.checkHosts(hosts);
        connections.putAll(conns);
    }

    @Override
    public InetAddress getAddress() {
        return address;
    }

    @Override
    public void backupSession(String sessionReference, Host host) throws InvalidBackupInformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
