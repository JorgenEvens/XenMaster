/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.entities;

import java.util.UUID;

/**
 * 
 * @created Oct 9, 2011
 * @author double-u
 */
public class VM extends net.wgr.core.dao.Object {
    
    protected UUID id;
    

    @Override
    public String getColumnFamily() {
        return "vms";
    }

    @Override
    public String getKeyFieldName() {
        return "id";
    }
    
}
