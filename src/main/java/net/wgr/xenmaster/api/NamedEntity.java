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

/**
 * 
 * @created Oct 23, 2011
 * @author double-u
 */
public class NamedEntity extends XenApiEntity {
    
    protected String name, description;

    public NamedEntity(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public NamedEntity(String ref) {
        super(ref);
    }

    public String getDescription() {
        description = value(description, "get_name_description");
        return description;
    }

    public void setDescription(String description) throws BadAPICallException {
        this.description = setter(description, "set_name_description");
    }

    public String getName() {
        name = value(name, "get_name_label");
        return name;
    }

    public void setName(String name) throws BadAPICallException {
        this.name = setter(name, "set_name_label");
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "name_label");
        map.put("description", "name_description");
        return map;
    }
    
}
