/*
 * Store.java
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
package org.xenmaster.api.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

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
