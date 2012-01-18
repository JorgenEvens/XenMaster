/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import net.wgr.core.ReflectionUtils;
import net.wgr.xenmaster.api.util.APIUtil;
import org.apache.log4j.Logger;

/**
 * 
 * @created Jan 18, 2012
 * @author double-u
 */
public abstract class XenApiMapField {
    
    public XenApiMapField() {
        
    }
    
    public XenApiMapField(Map<String, String> values) {
        fromMap(values);
    }
    
    public Map<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();
        
        for (Field f : ReflectionUtils.getAllFields(getClass())) {
            if (Modifier.isTransient(f.getModifiers())) continue;
            
            f.setAccessible(true);
            try {
                map.put(f.getName(), f.get(this).toString());
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(getClass()).error("Failed to retrieve field value", ex);
                continue;
            }
        }
        
        return map;
    }
    
    public void fromMap(Map<String, String> values) {
        for (Field f : ReflectionUtils.getAllFields(getClass())) {
            
            if (!values.containsKey(f.getName()) || values.get(f.getName()) == null) continue;
            
            if (Modifier.isTransient(f.getModifiers())) continue;
            
            f.setAccessible(true);
            try {
                f.set(this, APIUtil.deserializeToTargetType(values.get(f.getName()), f.getType()));
            } catch (Exception ex) {
                Logger.getLogger(getClass()).error("Failed to set field value", ex);
            }
        }
    }
    
    public Map<String, String> fieldNameInterpretation() {
        HashMap<String, String> map = new HashMap<>();
        return map;
    }
}
