/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.HashMap;
import java.util.Map;
import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.controller.Controller;

/**
 * 
 * @created Oct 16, 2011
 * @author double-u
 */
public class SR extends XenApiEntity {

    protected String name, description;
    @Fill
    protected Object[] allowedOperations;
    @Fill
    protected Map<String, String> currentOperations;
    protected long virtualAllocation, physicalUtilisation;
    protected boolean shared;
    @Fill
    protected Map<String, String> smConfig, otherConfig;
    protected String type, contentType;
    protected boolean localCache;

    public SR(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public SR(String ref) {
        super(ref);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = setter(description, "set_label_description");
    }

    public boolean isLocalCache() {
        return localCache;
    }

    public void setLocalCache(boolean localCache) {
        this.localCache = localCache;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getOtherConfig() {
        return otherConfig;
    }

    public void setOtherConfig(Map<String, String> otherConfig) {
        this.otherConfig = otherConfig;
    }

    public Map<String, String> getSmConfig() {
        return smConfig;
    }

    public void setSmConfig(Map<String, String> smConfig) {
        this.smConfig = smConfig;
    }

    public Object[] getAllowedOperations() {
        return allowedOperations;
    }

    public String getContentType() {
        return contentType;
    }

    public Map<String, String> getCurrentOperations() {
        return currentOperations;
    }

    public long getPhysicalUtilisation() {
        return physicalUtilisation;
    }

    public boolean isShared() {
        return shared;
    }

    public Type getType() {
        return Type.valueOf(type);
    }

    public long getVirtualAllocation() {
        return virtualAllocation;
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "name_label");
        map.put("description", "name_description");
        map.put("localCache", "local_cache_enabled");
        return map;
    }

    public void create(Host host, Map<String, String> deviceConfig, Type type, String contentType, boolean shared, int size) throws BadAPICallException {
        if (reference != null) throw new IllegalArgumentException("Object reference is set");
        if (host == null || deviceConfig == null || name == null) throw new IllegalArgumentException("Some essential arguments haven't been supplied");
        if (smConfig == null) smConfig = new HashMap<>();
        Controller.dispatch("SR.create", host.getUUID().toString(), deviceConfig, "" + size, name, description, type.name().toLowerCase(), contentType, shared);
    }

    public static enum Type {
        EXT, File, LVM
    }
}
