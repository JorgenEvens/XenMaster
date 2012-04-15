/*
 * Network.java
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xenmaster.controller.BadAPICallException;

/**
 * 
 * @created Oct 23, 2011
 * @author double-u
 */
public class Network extends NamedEntity {

    protected int mtu;
    protected String bridge;
    @Fill
    protected Object[] allowedOperations, vifs, pifs;
    @Fill
    protected Map<String, Object> currentOperations, otherConfig;
    
    public Network() {
        
    }

    public Network(String ref) {
        super(ref);
    }

    public Network(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public void create(String bridge) throws BadAPICallException {
        this.bridge = bridge;
        HashMap<String, Object> args = new HashMap<>();
        args.put("bridge", bridge);
        if (otherConfig == null) {
            otherConfig = new HashMap<>();
        }
        args.put("other_config", otherConfig);
        dispatch("create", args);
    }

    public static List<Network> getAll() throws BadAPICallException {
        return getAllEntities(Network.class);
    }

    public int getMTU() {
        return mtu;
    }

    public void setMTU(int MTU) {
        this.mtu = MTU;
    }

    public String getBridge() {
        return bridge;
    }
}
