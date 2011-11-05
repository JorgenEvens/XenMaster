/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring.data;

/**
 * 
 * @created Oct 31, 2011
 * @author double-u
 */
public class DataSet {

    protected String name;
    protected Type type;
    protected double minimalHeartbeat;
    protected String min, max;
    protected long lastDS;
    protected double value;
    protected int unknownSec;

    public static enum Type {
        GAUGE, DERIVE
    }
}
