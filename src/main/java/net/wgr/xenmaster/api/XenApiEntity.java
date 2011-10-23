/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.wgr.core.ReflectionUtils;
import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.controller.Controller;
import org.apache.commons.collections.CollectionUtils;
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
        this(ref, ref != null);
    }

    public XenApiEntity(String ref, boolean autoFill) {
        this.reference = ref;
        if (autoFill) {
            fillOut(getAPIName(), null);
        }
    }

    protected String getAPIName() {
        String sn = getClass().getSimpleName();
        if (sn.toUpperCase().equals(sn)) {
            return sn;
        } else {
            return sn.toLowerCase();
        }
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

    public String getIDString() {
        return getUUID().toString();
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

    protected <T> T value(T obj, String name, Object... params) {
        if (obj != null) {
            return obj;
        } else {
            try {
                return (T) dispatch(getAPIName() + "." + name, params);
            } catch (BadAPICallException ex) {
                return null;
            }
        }
    }

    protected <T> String setter(T obj, String name) throws BadAPICallException {
        if (reference != null && !reference.isEmpty() && name == null) {
            if (obj == null) {
                throw new IllegalArgumentException("Null value is not allowed for " + name);
            } else {
                dispatch(getAPIName() + "." + name, obj);
            }
        }

        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof XenApiEntity) {
            return ((XenApiEntity) obj).getReference();
        } else {
            return obj.toString();
        }
    }

    protected Object dispatch(String methodName, Object... params) throws BadAPICallException {
        ArrayList arr = new ArrayList();
        if (this.reference != null) {
            arr.add(this.reference);
        }
        CollectionUtils.addAll(arr, params);
        try {
            return Controller.dispatch(getAPIName() + "." + methodName, arr.toArray());
        } catch (BadAPICallException ex) {
            String errMsg = "";

            // Check if we can handle it
            switch (ex.getMessage()) {
                case "OPERATION_NOT_ALLOWED":
                    errMsg = "Tried to perform an unallowed operation";
                    break;
                case "OTHER_OPERATION_IN_PROGRESS":
                    errMsg = "Another operation is in progress";
                    break;
                default:
                    throw ex;
            }

            Logger.getLogger(getClass()).error(errMsg, ex);
        }
        return null;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Fill {

        boolean fillAPIObject() default false;
    }

    public void fillOut(Map<String, Object> data) {
        fillOut(getAPIName(), data);
    }

    protected final void fillOut(String className, Map<String, Object> data) {

        if (data == null) {
            try {
                data = (Map<String, Object>) Controller.dispatch((className == null ? getClass().getSimpleName().toLowerCase() : className) + ".get_record", this.reference);
            } catch (BadAPICallException ex) {
                Logger.getLogger(getClass()).error(ex);
            }
        }

        if (data == null) {
            throw new Error("Get record failed");
        }

        Map<String, String> interpretation = interpretation();

        for (Field f : ReflectionUtils.getAllFields(getClass())) {

            // MyNameIsHans -> my_name_is_hans
            String processedName = "";
            if (interpretation.containsKey(f.getName())) {
                processedName = interpretation.get(f.getName());
            } else {
                // Try exact match
                if (data.keySet().contains(f.getName())) {
                    processedName = f.getName();
                } else {
                    processedName = f.getName().replaceAll("(.)(\\p{Lu})", "$1_$2").toLowerCase();
                }
            }

            Object value = null;
            for (String key : data.keySet()) {
                if (key.equals(processedName)) {
                    value = data.get(key);
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
                        if ((value.getClass().getName().equals("java.lang.String"))) {
                            // The API is nuts
                            String corrected = value.toString().replace("1", "true").replace("0", "false");
                            f.setBoolean(this, Boolean.parseBoolean(corrected));
                        } else {
                            f.setBoolean(this, (boolean) value);
                        }
                        break;
                    case "int":
                        // The API returns numeric values as String ><
                        if (value.getClass().getName().equals("java.lang.String")) {
                            f.set(this, Integer.parseInt(value.toString()));
                        } else {
                            f.setInt(this, (int) value);
                        }
                        break;
                    case "long":
                        if (value.getClass().getName().equals("java.lang.String")) {
                            f.set(this, Long.parseLong(value.toString()));
                        } else {
                            f.setLong(this, (long) value);
                        }
                        break;
                    case "float":
                        f.setFloat(this, (float) value);
                        break;
                    default:
                        if (f.getType().isEnum()) {
                            for (Object obj : f.getType().getEnumConstants()) {
                                if (obj.toString().toLowerCase().equals(value.toString().toLowerCase())) {
                                    f.set(this, obj);
                                }
                            }
                        } else if (f.isAnnotationPresent(Fill.class)) {
                            Object casted = f.getType().cast(value);
                            if (XenApiEntity.class.isAssignableFrom(f.getType()) && f.getAnnotation(Fill.class).fillAPIObject()) {
                            }
                            f.set(this, casted);
                        }
                        break;
                }
            } catch (IllegalAccessException | IllegalArgumentException ex) {
                Logger.getLogger(getClass()).error(ex);
            }
        }
    }
}
