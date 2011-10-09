/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.entities;

import java.util.Collection;
import java.util.UUID;
import net.wgr.core.dao.Required;
import net.wgr.core.data.Retrieval;

/**
 * 
 * @created Oct 8, 2011
 * @author double-u
 */
public class Host extends net.wgr.core.dao.Object {
    
    @Required
    protected UUID id;
    @Required
    protected String hostName;
    protected String macAddress;

    @Override
    public String getColumnFamily() {
        return "hosts";
    }

    @Override
    public String getKeyFieldName() {
        return "id";
    }
    
    public UUID getId() {
        return id;
    }

    public String getHostName() {
        return hostName;
    }

    public String getMacAddress() {
        return macAddress;
    }
    
    public Collection<Host> getAll() {
        Collection<Host> hosts;
        hosts = Retrieval.getRowsAs(Host.class, Retrieval.getAllRowsFromColumnFamily(getColumnFamily())).values();
        return hosts;
    }
    
}
