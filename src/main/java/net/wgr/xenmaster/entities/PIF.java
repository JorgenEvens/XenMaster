/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.entities;

/**
 * Physical (Network) Interface
 * @created Oct 5, 2011
 * @author double-u
 */
public class PIF extends XenApiEntity {
    
    protected int MTU;
    protected int VLAN;
    protected String device;
    protected String MAC;

    public PIF(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public PIF(String ref) {
        super(ref);
    }
    
}
