/*
 * XenApiMapField.java
 * Copyright (C) 2011,2012 Wannes De Smet
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.xenmaster.api;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import net.wgr.core.ReflectionUtils;
import org.apache.log4j.Logger;
import org.xenmaster.api.util.APIUtil;

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
