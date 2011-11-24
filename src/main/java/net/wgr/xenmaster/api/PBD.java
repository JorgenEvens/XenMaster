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
    protected boolean attached;
    protected String SR, host;

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

    public void create(SR sr, Host host, Map<String, String> cfg) throws BadAPICallException {
        HashMap<String, Object> args = new HashMap<>();
        args.put("SR", sr.getReference());
        args.put("host", host.getReference());
        args.put("device_config", cfg);
        if (otherConfig != null) {
            args.put("other_config", otherConfig);
        }
        this.reference = (String) dispatch("create", args);
    }

    public SR getSR() {
        return new SR(SR);
    }

    public static List<PBD> getAll() throws BadAPICallException {
        Map<String, Object> records = (Map) Controller.dispatch("PBD.get_all_records");
        ArrayList<PBD> objects = new ArrayList<>();
        for (Map.Entry<String, Object> entry : records.entrySet()) {
            PBD obj = new PBD(entry.getKey(), false);
            obj.fillOut((Map) entry.getValue());
            objects.add(obj);
        }
        return objects;
    }

    public boolean isAttached() {
        return attached;
    }

    public Host getHost() {
        return new Host(host);
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("attached", "currently_attached");
        return map;
    }
}
