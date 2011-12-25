/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import net.wgr.xenmaster.api.XenApiEntity;
import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.controller.Controller;
import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.config.FluentConfiguration;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * 
 * @created Dec 19, 2011
 * @author double-u
 */
public class CachingFacility {

    protected Mode mode;
    protected Cache<String, XenApiEntity> cache;
    protected CopyOnWriteArrayList<Class> loadedEntityClasses;
    private static CachingFacility instance;

    private CachingFacility() {
        this.mode = Mode.LAZY;
        this.cache = buildCache();
        this.loadedEntityClasses = new CopyOnWriteArrayList<>();
    }
    
    protected Cache buildCache() {
        FluentConfiguration fc = new FluentConfiguration(null);
        EmbeddedCacheManager ecm = new DefaultCacheManager(fc.build());
        return ecm.getCache();
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

    protected <T extends XenApiEntity> void heatCache(Class<T> target) {
        try {
            Map<String, Object> objects = (Map<String, Object>) Controller.dispatch(XenApiEntity.getAPIName(target) + ".get_all_records");
            Constructor<T> ctor = target.getConstructor(String.class, boolean.class);
            for (Map.Entry<String, Object> entry : objects.entrySet()) {
                T obj = ctor.newInstance(entry.getKey(), false);
                obj.fillOut((Map<String, Object>) entry.getValue());
                cache.put(entry.getKey(), obj);
            }
            loadedEntityClasses.add(target);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(getClass()).debug("Failed to contruct object of type " + target.getCanonicalName(), ex);
        } catch (BadAPICallException ex) {
            Logger.getLogger(getClass()).debug(target.getCanonicalName() + " does not have a getAll method", ex);
        }
    }

    public static <T extends XenApiEntity> T get(String reference, Class<T> target) {
        return instance().getEntity(reference, target);
    }

    public boolean isCached(String reference, Class target) {
        return cache.containsKey(reference) && target != null && target.isAssignableFrom(cache.get(reference).getClass());
    }

    public <T extends XenApiEntity> T getEntity(String reference, Class<T> target) {
        if (!loadedEntityClasses.contains(target)) {
            heatCache(target);
        }

        if (cache.containsKey(reference)) {
            if (target != null && target.isAssignableFrom(cache.get(reference).getClass())) {
                return (T) cache.get(reference);
            } else {
                Logger.getLogger(getClass()).error("Cached entity has an illegal type " + cache.get(reference).getClass().getCanonicalName() + " instead of " + target.getCanonicalName());
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
