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
 * @created Oct 23, 2011
 * @author double-u
 */
public class Network extends NamedEntity {
    
    protected int MTU;
    protected String bridge;
    @Fill
    protected Object[] allowedOperations, VIFs, PIFs;
    @Fill
    protected Map<String, Object> currentOperations, otherConfig;

    public Network(String ref) {
        super(ref);
    }

    public Network(String ref, boolean autoFill) {
        super(ref, autoFill);
    }
    
    public void create(String bridge) throws BadAPICallException {
        this.bridge = bridge;
        HashMap<String, Object> args = new HashMap<>();
        args.put("bridge", bridge);
        if (otherConfig == null) otherConfig = new HashMap<>();
        args.put("other_config", otherConfig);
        dispatch("create", args);
    }
    
    public static List<Network> getAll() throws BadAPICallException {
        return getAllEntities(Network.class);
    }

    public int getMTU() {
        return MTU;
    }

    public void setMTU(int MTU) {
        this.MTU = MTU;
    }

    public String getBridge() {
        return bridge;
    }
}
