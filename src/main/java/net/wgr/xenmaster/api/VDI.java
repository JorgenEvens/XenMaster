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

/**
 * 
 * @created Oct 10, 2011
 * @author double-u
 */
public class VDI extends NamedEntity {

    @ConstructorArgument
    protected String sr;
    protected Object[] VBDs;
    @ConstructorArgument
    protected long virtualSize;
    protected long physicalUtilization;
    @ConstructorArgument
    protected Type type;
    @ConstructorArgument
    protected boolean shareable, readOnly;
    protected String securityLabel;
    @ConstructorArgument
    protected Map<String, String> otherConfig;
    protected final static int megabyte = 1024 * 1024;
    
    public VDI() {
        
    }

    public VDI(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public VDI(String ref) {
        super(ref);
    }

    public SR getSR() {
        this.sr = value(sr, "get_SR");
        return new SR(sr, false);
    }

    // todo watch closely
    public List<VBD> getVBDs() {
        VBDs = value(VBDs, "get_VBDs");
        ArrayList<VBD> vbds = new ArrayList<>();
        for (Object vbd : VBDs) {
            vbds.add(new VBD((String) vbd, false));
        }
        return vbds;
    }

    public String create(double sizeInMb, Type type, SR repo, boolean shareable, boolean readOnly) throws BadAPICallException {
        this.virtualSize = (int) (sizeInMb * (megabyte));
        this.type = type;
        this.sr = repo.getReference();
        this.shareable = shareable;
        this.readOnly = readOnly;
        
        this.reference = (String) dispatch("create", collectConstructorArgs());
        return this.reference;
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
        return getAllEntities(VDI.class);
    }

    public long getPhysicalUtilization() {
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

    public long getVirtualSize() {
        return virtualSize;
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = (HashMap<String, String>) super.interpretation();
        map.put("name", "name_label");
        map.put("description", "name_description");
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
