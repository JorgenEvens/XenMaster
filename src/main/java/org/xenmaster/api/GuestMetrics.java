/*
 * GuestMetrics.java
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xenmaster.controller.BadAPICallException;
import org.xenmaster.controller.Controller;

/**
 * 
 * @created Oct 9, 2011
 * @author double-u
 */
public class GuestMetrics extends XenApiEntity {

    protected Map<String, String> osVersion, pvDriversVersion, memory, disks, networks, other;
    protected Date lastUpdate;

    public GuestMetrics(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public GuestMetrics(String ref) {
        super(ref);
    }

    @Override
    protected String getAPIName() {
        return "VM_guest_metrics";
    }
    
    public static List<GuestMetrics> getAll() {
        try {
            Object[] gms = (Object[]) Controller.dispatch("VM_guest_metrics.get_all");
            for (Object gmr : gms){
                
            }
        } catch (BadAPICallException ex) {
            Logger.getLogger(GuestMetrics.class).error(ex);
        }
        return null;
    }

    public Map<String, String> getPVdriversVersion() {
        return pvDriversVersion;
    }

    public Map<String, String> getDisks() {
        return disks;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Map<String, String> getMemory() {
        return memory;
    }

    public Map<String, String> getNetworks() {
        return networks;
    }

    public Map<String, String> getOsVersion() {
        return osVersion;
    }

    public Map<String, String> getOther() {
        return other;
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("pvDriversVersion", "PV_drivers_version");
        return map;
    }
}
