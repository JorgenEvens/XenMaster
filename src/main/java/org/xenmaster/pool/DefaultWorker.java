/*
 * DefaultWorker.java
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
package org.xenmaster.pool;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.xenmaster.connectivity.Connections;
import org.xenmaster.entities.Host;
import org.xenmaster.manager.Preflight;

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
