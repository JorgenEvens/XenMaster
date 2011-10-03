/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.entities;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
        this.reference = ref;
    }

    public String getReference() {
        return reference;
    }

    public UUID getUUID() {
        return UUID.fromString(uuid);
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid.toString();
    }

    protected void fillOut() {
        Map<String, Object> result = (Map<String, Object>) Controller.dispatch(getClass().getSimpleName().toLowerCase() + ".get_record", this.reference);

        for (Field f : ReflectionUtils.getAllFields(getClass())) {
            // MyNameIsHans -> my_name_is_hans
            String processedName = f.getName().replaceAll("(.)(\\p{Lu})", "$1_$2").toLowerCase();
            Object value = null;
            for (String key : result.keySet()) {
                if (key.toLowerCase().equals(processedName)) value = result.get(key);
            }
            if (value == null) continue;

            if (Modifier.isProtected(f.getModifiers()) || Modifier.isPrivate(f.getModifiers())) {
                f.setAccessible(true);
            }

            try {
                switch (f.getType().getName()) {
                    case "java.lang.String":
                        f.set(this, value);
                        break;
                }
            } catch (IllegalAccessException | IllegalArgumentException ex) {
                Logger.getLogger(getClass()).error(ex);
            }
        }
    }
}
