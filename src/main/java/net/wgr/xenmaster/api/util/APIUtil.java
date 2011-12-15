/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import net.wgr.xenmaster.api.XenApiEntity;

/**
 * 
 * @created Dec 14, 2011
 * @author double-u
 */
public class APIUtil {

    public static Object deserializeToTargetType(Object value, Class type) throws Exception {
        switch (type.getSimpleName().toLowerCase()) {
            case "boolean":
                return Boolean.parseBoolean(value.toString());
            case "integer":
            case "int":
                return Integer.parseInt(value.toString());
            case "long":
                return Long.parseLong(value.toString());
            case "double":
                return Double.parseDouble(value.toString());
            default:
                if (type.isEnum()) {
                    String ucase = value.toString().toUpperCase();
                    boolean found = false;
                    for (Object enumType : type.getEnumConstants()) {
                        if (enumType.toString().toUpperCase().equals(ucase)) {
                            found = true;
                            return enumType;
                        }
                    }
                    if (!found) {
                        throw new IllegalArgumentException("Argument value does not belong to enum values of " + type.getCanonicalName());
                    }
                } else if (type.isArray()) {
                    // FIXME : This can break all too easily
                    Class t = type.getComponentType();
                    Object[] src = (Object[]) value;
                    Object[] arr = (Object[]) Array.newInstance(t, src.length);
                    for (int i = 0; i < src.length; i++) {
                        arr[i] = deserializeToTargetType(src[i], t);
                    }
                    return arr;
                } else if (InetAddress.class.isAssignableFrom(type)) {
                    return InetAddress.getByName(value.toString());
                } else if (XenApiEntity.class.isAssignableFrom(type)) {
                    Constructor c = type.getConstructor(String.class, boolean.class);
                    return c.newInstance(value.toString(), false);
                }
                break;
        }
        return value;
    }
}
