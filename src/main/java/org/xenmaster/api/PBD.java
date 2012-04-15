/*
 * PBD.java
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
 * @created Oct 22, 2011
 * @author double-u
 */
public class PBD extends XenApiEntity {

    @Fill
    protected Map<String, Object> deviceConfig, otherConfig;
    protected boolean plugged;
    protected String sr, host;
    
    public PBD() {
        
    }

    public PBD(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public PBD(String ref) {
        super(ref);
    }

    public void plug() throws BadAPICallException {
        dispatch("plug");
    }

    public void unplug() throws BadAPICallException {
        dispatch("unplug");
    }

    public String create(SR sr, Host host, Map<String, String> cfg) throws BadAPICallException {
        HashMap<String, Object> args = new HashMap<>();
        args.put("SR", sr.getReference());
        args.put("host", host.getReference());
        args.put("device_config", cfg);
        if (otherConfig != null) {
            args.put("other_config", otherConfig);
        }
        this.reference = (String) dispatch("create", args);
        return this.reference;
    }

    public SR getSR() {
        return new SR(sr);
    }

    public static List<PBD> getAll() throws BadAPICallException {
        return getAllEntities(PBD.class);
    }

    public boolean isPlugged() {
        return plugged;
    }

    public Host getHost() {
        return new Host(host);
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("plugged", "currently_attached");
        return map;
    }
}
