/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import org.joda.time.Period;

/**
 * 
 * @created Jan 18, 2012
 * @author double-u
 */
public abstract class Sensor implements Runnable {
    public abstract Period getDefaultSchedule();
}
