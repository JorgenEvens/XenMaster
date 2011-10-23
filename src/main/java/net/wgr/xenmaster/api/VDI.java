/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.controller.Controller;

/**
 * 
 * @created Oct 10, 2011
 * @author double-u
 */
public class VDI extends XenApiEntity {

    protected String name, description;
    protected String SR;
    protected Object[] VBDs;
    protected int virtualSize, physicalUtilization;
    protected Type type;
    protected boolean shareable, readOnly;
    protected String securityLabel;
    protected static int megabyte = 1024 * 1024;

    public VDI(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public VDI(String ref) {
        super(ref);
    }

    public String getStorageRepository() {
        return SR;
    }

    public List<VBD> getVBDs() {
        VBDs = value(VBDs, "get_VBDs");
        ArrayList<VBD> vbds = new ArrayList<>();
        for (Object vbd : VBDs) {
            vbds.add(new VBD((String) vbd, false));
        }
        return vbds;
    }

    public void create(double sizeInMb, Type type, SR repo, boolean shareable, boolean readOnly) throws BadAPICallException {
        // Todo allow create with Map of args
        HashMap<String, Object> values = new HashMap<>();
        values.put("name_label", name);
        values.put("name_description", (description == null ? "" : description));
        values.put("virtual_size", "" + Math.round(sizeInMb * megabyte));
        values.put("type", type.name().toLowerCase());
        values.put("SR", repo.getReference());
        values.put("sharable", shareable);
        values.put("read_only", readOnly);
        values.put("other_config", new HashMap<>());
        this.reference = (String) Controller.dispatch("VDI.create", values);
    }
    
    public VDI snapshot() throws BadAPICallException {
        String snapshotRef = (String) dispatch("snapshot", new HashMap<>());
        // TODO register with data backend
        // List appropriatly in UI
        return new VDI(snapshotRef, false);
    }
    
    public VDI duplicate() throws BadAPICallException {
        String clone = (String) dispatch("clone", new HashMap<>());
        return new VDI(clone, false);
    }
    
    public VDI copy(SR sr) throws BadAPICallException {
        String copy = (String) dispatch("copy", sr.getReference());
        return new VDI(copy, false);
    }
     
    public void resize(double newSizeInMB, boolean online) throws BadAPICallException {
        dispatch((online ? "resize_online" : "resize"), "" + Math.round(newSizeInMB + megabyte));
    }
    
    public void forget() throws BadAPICallException {
        dispatch("forget");
    }
    
    public void destroy() throws BadAPICallException {
        dispatch("destroy");
    }
    
    public static List<VDI> getAll() throws BadAPICallException {
        ArrayList<VDI> VDIs = new ArrayList<>();
        Map<String, Object> vdis = (HashMap<String, Object>) Controller.dispatch("VDI.get_all_records");
        for (Map.Entry<String, Object> entry : vdis.entrySet()) {
            VDI vdi = new VDI(entry.getKey(), false);
            vdi.fillOut((HashMap<String, Object>) entry.getValue());
            VDIs.add(vdi);
        }
        return VDIs;
    }

    public String getDescription() {
        description = value(description, "get_name_description");
        return description;
    }

    public String getName() {
        name = value(name, "get_name_label");
        return name;
    }

    public int getPhysicalUtilization() {
        physicalUtilization = value(physicalUtilization, "get_physical_utilization");
        return physicalUtilization;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public String getSecurityLabel() {
        return securityLabel;
    }

    public boolean isShareable() {
        return shareable;
    }

    public Type getType() {
        return type;
    }

    public int getVirtualSize() {
        return virtualSize;
    }

    public void setDescription(String description) {
        this.description = setter(description, "name_description");
    }

    public void setName(String name) {
        this.name = setter(name, "name_label");
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "name_label");
        map.put("description", "name_description");
        // TODO file bug
        map.put("shareable", "sharable");
        return map;
    }

    public static enum Type {

        /**
         * The value does not belong to this enumeration
         */
        UNRECOGNIZED,
        /**
         * a disk that may be replaced on upgrade
         */
        SYSTEM,
        /**
         * a disk that is always preserved on upgrade
         */
        USER,
        /**
         * a disk that may be reformatted on upgrade
         */
        EPHEMERAL,
        /**
         * a disk that stores a suspend image
         */
        SUSPEND,
        /**
         * a disk that stores VM crashdump information
         */
        CRASHDUMP,
        /**
         * a disk used for HA storage heartbeating
         */
        HA_STATEFILE,
        /**
         * a disk used for HA Pool metadata
         */
        METADATA,
        /**
         * a disk used for a general metadata redo-log
         */
        REDO_LOG
    }
}
