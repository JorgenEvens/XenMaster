/*
 * DefaultMaster.java
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.wgr.core.data.Retrieval;
import org.xenmaster.entities.Host;

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
            if (h.isActive()) {
                hosts.add(h);
            }
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
