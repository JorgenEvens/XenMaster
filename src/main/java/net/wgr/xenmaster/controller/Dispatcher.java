/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.controller;

import java.util.ArrayList;
import java.util.Map;
import net.wgr.xenmaster.connectivity.Connection;
import org.apache.commons.collections.CollectionUtils;

/**
 * 
 * @created Oct 2, 2011
 * @author double-u
 */
public class Dispatcher {
    protected Connection conn;
    
    public Dispatcher() {
        this.conn = new Connection();
    }
    
    public Object dispatch(String methodName, Object[] params) {
        ArrayList list = new ArrayList();
        CollectionUtils.addAll(list, params);
        Map result = this.conn.executeCommand(methodName, list);
        
        if (result.get("Status").equals("Success")) {
            return result.get("Value");
        } else {
            return null;
        }
    } 
}
