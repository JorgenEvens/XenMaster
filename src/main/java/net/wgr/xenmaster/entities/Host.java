/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.entities;

/**
 * 
 * @created Oct 2, 2011
 * @author double-u
 */
public class Host extends XenApiEntity {
    
    protected boolean enabled;
    
    protected String apiVersionMajor, apiVersionMinor;
    protected String apiVersionVendor;
    protected String nameLabel, schedPolicy, nameDescription;

    public Host(String ref) {
        super(ref);
        fillOut();
    }
   
}
