/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @created Oct 6, 2011
 * @author double-u
 */
public class HostMetrics extends XenApiEntity {
    protected int totalMemory, freeMemory;
    protected Date lastUpdate;

    public HostMetrics(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public HostMetrics(String ref) {
        super(ref);
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> m = new HashMap<>();
        m.put("actualMemory", "memory_actual");
        return m;
    }


}
