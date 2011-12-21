/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import net.wgr.xenmaster.api.XenApiEntity;
import org.apache.log4j.Logger;

/**
 * 
 * @created Dec 19, 2011
 * @author double-u
 */
public class CachingFacility {

    protected Mode mode;
    protected ConcurrentHashMap<String, XenApiEntity> cache;
    private static CachingFacility instance;

    private CachingFacility() {
        this.mode = Mode.LAZY;
        this.cache = new ConcurrentHashMap<>();
    }

    public static CachingFacility instance() {
        if (instance == null) {
            instance = new CachingFacility();
        }
        return instance;
    }

    public static enum Mode {

        PREHEAT, LAZY
    }

    protected void preheat() {
    }
    
    public static <T extends XenApiEntity> T get(String reference, Class<T> target) {
        return instance().getEntity(reference, target);
    }

    public <T extends XenApiEntity> T getEntity(String reference, Class<T> target) {
        if (cache.containsKey(reference)) {
            if (target != null && target.isAssignableFrom(cache.get(reference).getClass())) {
                return (T) cache.get(reference);
            } else {
                Logger.getLogger(getClass()).error("Cached entity has an illegal type :" + cache.get(reference).getClass().getCanonicalName() + " instead of " + target.getCanonicalName());
                return null;
            }
        }

        try {
            Constructor c = target.getConstructor(String.class, boolean.class);
            T newObject = (T) c.newInstance(reference, !reference.isEmpty());
            return newObject;
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | NoSuchMethodException ex) {
            Logger.getLogger(getClass()).error("Failed to initialize object of type " + target.getCanonicalName(), ex);
        }

        return null;
    }
}
