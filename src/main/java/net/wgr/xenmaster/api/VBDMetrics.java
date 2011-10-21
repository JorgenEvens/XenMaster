/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.Date;

/**
 * 
 * @created Oct 20, 2011
 * @author double-u
 */
public class VBDMetrics extends XenApiEntity {
    
    protected float readKbs, writeKbs;
    protected Date lastUpdated;

    public VBDMetrics(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public VBDMetrics(String ref) {
        super(ref);
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    // TODO rename
    public float getReadKbs() {
        return readKbs;
    }

    public float getWriteKbs() {
        return writeKbs;
    }
}
