/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.controller.Controller;

/**
 * 
 * @created Oct 18, 2011
 * @author double-u
 */
public class Pool extends NamedEntity {
    
    protected Object[] metadataVDIs;
    protected boolean wlbEnabled, haEnabled;
    protected int hostFailuresToTolerate;
    protected String defaultSR, suspendImageSR, crashDumpSR;
    protected String redoLogVDI;
    protected boolean overcommitingAllowed, overcommitted;
    protected String master;
    @Fill
    protected Map<String, String> restrictions, otherConfig;

    public Pool(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public Pool(String ref) {
        super(ref);
    }
    
    public Host getMaster() {
        master = value(master, "get_master");
        return new Host(master);
    }
    
    public SR getDefaultSR() {
        defaultSR = value(defaultSR, "get_default_SR");
        return new SR(defaultSR);
    }
    
    public void setDefaultSR(SR newDefault) throws BadAPICallException {
        defaultSR = setter(newDefault, "set_default_SR");
    }
    
    public VDI getRedoLogVDI() {
        redoLogVDI = value(redoLogVDI, "get_redo_log_vdi");
        return new VDI(redoLogVDI);
    }

    public static List<Pool> getAll() throws BadAPICallException {
        Map<String, Object> pools = (Map) Controller.dispatch("pool.get_all_records");
        ArrayList<Pool> mudPools = new ArrayList<>();
        for (Map.Entry<String, Object> entry : pools.entrySet()) {
            Pool p = new Pool(entry.getKey(), false);
            p.fillOut((Map) entry.getValue());
            mudPools.add(p);
        }
        return mudPools;
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = (HashMap<String, String>) super.interpretation();
        map.put("overcomittingAllowed", "ha_allow_overcommit");
        map.put("overcommited", "ha_overcommitted");
        map.put("hostFailuresToTolerate", "ha_host_failures_to_tolerate");
        
        return map;
    }
    
    
}
