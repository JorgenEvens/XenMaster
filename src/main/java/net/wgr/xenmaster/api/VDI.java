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

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getPhysicalUtilization() {
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
