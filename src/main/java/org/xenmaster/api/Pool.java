/*
 * Pool.java
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

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xenmaster.controller.BadAPICallException;
import org.xenmaster.controller.Controller;

/**
 * 
 * @created Oct 18, 2011
 * @author double-u
 */
public class Pool extends NamedEntity {

    protected Object[] metadataVDIs;
    protected boolean wlbEnabled, haEnabled;
    protected int hostFailuresToTolerate;
    protected String defaultSR, suspendImageSR, crashDumpSR;
    protected String redoLogVDI;
    protected boolean overcommitingAllowed, overcommitted;
    protected String master;
    @Fill
    protected Map<String, String> restrictions, otherConfig;
    
    public Pool() {
        
    }

    public Pool(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public Pool(String ref) {
        super(ref);
    }

    public void addHostWithAddress(InetAddress address, String user, String password, boolean force) throws BadAPICallException {
        Controller.dispatch((force ? "pool.join_force" : "pool.join"), address.getCanonicalHostName(), user, password);
    }

    public void ejectHost(Host host) throws BadAPICallException {
        Controller.dispatch("pool.eject", host.getReference());
    }

    public void designateNewMaster(Host newMaster) throws BadAPICallException {
        Controller.dispatch("pool.designate_new_master", newMaster.getReference());
    }

    public Host getMaster() {
        master = value(master, "get_master");
        return new Host(master);
    }

    public SR getDefaultSR() {
        defaultSR = value(defaultSR, "get_default_SR");
        return new SR(defaultSR);
    }

    public void setDefaultSR(SR newDefault) throws BadAPICallException {
        defaultSR = setter(newDefault, "set_default_SR");
    }

    public VDI getRedoLogVDI() {
        redoLogVDI = value(redoLogVDI, "get_redo_log_vdi");
        return new VDI(redoLogVDI);
    }

    public static List<Pool> getAll() throws BadAPICallException {
        return getAllEntities(Pool.class);
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = (HashMap<String, String>) super.interpretation();
        map.put("overcomittingAllowed", "ha_allow_overcommit");
        map.put("overcommited", "ha_overcommitted");
        map.put("hostFailuresToTolerate", "ha_host_failures_to_tolerate");

        return map;
    }
}
