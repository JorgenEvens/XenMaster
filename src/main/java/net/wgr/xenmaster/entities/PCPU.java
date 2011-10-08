/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.entities;

/**
 * 
 * @created Oct 7, 2011
 * @author double-u
 */
public class PCPU extends XenApiEntity {
    protected int number, speed;
    protected String vendor, modelname, stepping, flags, features;
    protected float utilisation;

    public PCPU(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public PCPU(String ref) {
        super(ref);
    }

    public String getFeatures() {
        return features;
    }

    public String getFlags() {
        return flags;
    }

    public String getModelname() {
        return modelname;
    }

    public int getNumber() {
        return number;
    }

    public int getSpeed() {
        return value(speed, "get_speed");
    }

    public String getStepping() {
        return stepping;
    }

    public float getUtilisation() {
        return value(utilisation, "get_utilisation");
    }

    public String getVendor() {
        return vendor;
    }
}
