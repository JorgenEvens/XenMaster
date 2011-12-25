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
 * @created Oct 22, 2011
 * @author double-u
 */
public class PBD extends XenApiEntity {

    @Fill
    protected Map<String, Object> deviceConfig, otherConfig;
    protected boolean plugged;
    protected String SR, host;
    
    public PBD() {
        
    }

    public PBD(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public PBD(String ref) {
        super(ref);
    }

    public void plug() throws BadAPICallException {
        dispatch("plug");
    }

    public void unplug() throws BadAPICallException {
        dispatch("unplug");
    }

    public String create(SR sr, Host host, Map<String, String> cfg) throws BadAPICallException {
        HashMap<String, Object> args = new HashMap<>();
        args.put("SR", sr.getReference());
        args.put("host", host.getReference());
        args.put("device_config", cfg);
        if (otherConfig != null) {
            args.put("other_config", otherConfig);
        }
        this.reference = (String) dispatch("create", args);
        return this.reference;
    }

    public SR getSR() {
        return new SR(SR);
    }

    public static List<PBD> getAll() throws BadAPICallException {
        return getAllEntities(PBD.class);
    }

    public boolean isPlugged() {
        return plugged;
    }

    public Host getHost() {
        return new Host(host);
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("plugged", "currently_attached");
        return map;
    }
}
