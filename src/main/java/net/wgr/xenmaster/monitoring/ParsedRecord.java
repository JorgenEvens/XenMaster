/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

/**
 * 
 * @created Oct 6, 2011
 * @author double-u
 */
public class ParsedRecord {
    protected float cpu, memory;
    
    public ParsedRecord(float cpu, float memory) {
        this.cpu = cpu;
        this.memory = memory;
    }
}
