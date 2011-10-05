/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.entities;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.wgr.core.ReflectionUtils;
import net.wgr.xenmaster.controller.Controller;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 2, 2011
 * @author double-u
 */
public class XenApiEntity {

    protected String reference;
    protected String uuid;

    public XenApiEntity(String ref) {
        this(ref, true);
    }

    public XenApiEntity(String ref, boolean autoFill) {
        this.reference = ref;
        if (autoFill) {
            fillOut(getAPIName());
        }
    }

    protected String getAPIName() {
        return getClass().getSimpleName().toLowerCase();
    }

    public String getReference() {
        return reference;
    }

    public UUID getUUID() {
        if (uuid.isEmpty()) {
            return null;
        }
        return UUID.fromString(uuid);
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid.toString();
    }

    /**
     * Allow us to give better names to some fields
     * @return 
     */
    protected Map<String, String> interpretation() {
        return new HashMap<>();
    }

    protected final void fillOut() {
        fillOut(null);
    }
    
    protected void notNull(Object obj) {
        
    }

    protected final void fillOut(String className) {
        Map<String, Object> result = (Map<String, Object>) Controller.dispatch((className == null ? getClass().getSimpleName().toLowerCase() : className) + ".get_record", this.reference);
        if (result == null) {
            return;
        }
        
        Map<String, String> interpretation = interpretation();

        for (Field f : ReflectionUtils.getAllFields(getClass())) {

            // MyNameIsHans -> my_name_is_hans
            String processedName = "";
            if (interpretation.containsKey(f.getName())) {
                processedName = interpretation.get(f.getName());
            } else {
                processedName = f.getName().replaceAll("(.)(\\p{Lu})", "$1_$2").toLowerCase();
            }

            Object value = null;
            for (String key : result.keySet()) {
                if (key.equals(processedName)) {
                    value = result.get(key);
                }
            }
            if (value == null) {
                continue;
            }

            if (Modifier.isProtected(f.getModifiers()) || Modifier.isPrivate(f.getModifiers())) {
                f.setAccessible(true);
            }

            try {
                switch (f.getType().getName()) {
                    case "java.lang.String":
                        f.set(this, value);
                        break;
                    case "boolean":
                        f.setBoolean(this, (boolean) value);
                        break;
                    case "int":
                        // The API returns numeric values as String ><
                        if (value.getClass().getName().equals("java.lang.String")) {
                            f.set(this, Integer.parseInt(value.toString()));
                        } else {
                            f.setInt(this, (int) value);
                        }
                        break;
                    default:
                        if (f.getType().isEnum()) {
                            for (Object obj : f.getType().getEnumConstants()) {
                                if (obj.toString().toLowerCase().equals(value.toString().toLowerCase())) {
                                    f.set(this, obj);
                                }
                            }
                        }
                        break;
                }
            } catch (IllegalAccessException | IllegalArgumentException ex) {
                Logger.getLogger(getClass()).error(ex);
            }
        }
    }
}
