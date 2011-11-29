/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api.store;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @created Nov 29, 2011
 * @author double-u
 */
public class Store {

    protected ConcurrentHashMap<String, Multimap<String, String>> store;
    private static Store instance;

    private Store() {
        store = new ConcurrentHashMap<>();
    }

    public static Store get() {
        if (instance == null) {
            instance = new Store();
        }
        return instance;
    }

    public void put(String store, String key, String value) {
        if (!this.store.containsKey(store)) {
            HashMultimap<String, String> hm = HashMultimap.create();
            this.store.put(store, hm);
        }

        this.store.get(store).put(key, value);
    }

    public Collection<String> get(String store, String key) {
        if (this.store.get(store) == null) {
            return new ArrayList<>();
        }
        return this.store.get(store).get(key);
    }
}
