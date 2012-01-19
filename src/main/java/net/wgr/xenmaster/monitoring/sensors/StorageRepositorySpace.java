/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring.sensors;

import net.wgr.xenmaster.api.SR;
import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.monitoring.LogEntry;
import net.wgr.xenmaster.monitoring.LogKeeper;
import net.wgr.xenmaster.monitoring.Sensor;
import org.apache.log4j.Logger;
import org.joda.time.Period;

/**
 * 
 * @created Jan 17, 2012
 * @author double-u
 */
public class StorageRepositorySpace extends Sensor {
    
    protected final static int THRESHOLD = 95;

    public Period getDefaultSchedule() {
        return new Period(6, 0, 0, 0);
    }

    @Override
    public void run() {
        try {
            for (SR sr : SR.getAll()) {
                if (!sr.getContentType().equals("user")) {
                    continue;
                }

                double physicalUsage = getUsage(sr, false);
                if (physicalUsage > THRESHOLD) {
                    LogEntry le = new LogEntry(sr.getReference(), "SR", "SR_ALLOCATION_EXCEEDS_THRESHOLD" , "SR_ALLOCATION_EXCEEDS_THRESHOLD_MESSAGE", new Object[] {sr.getName(), THRESHOLD}, LogEntry.Level.WARNING);
                    LogKeeper.log(le);
                }
                
                double virtualUsage = getUsage(sr, true);
                if (virtualUsage > THRESHOLD) {
                    LogEntry le = new LogEntry(sr.getReference(), "SR", "SR_ALLOCATION_EXCEEDS_THRESHOLD" , "SR_VIRTUAL_ALLOCATION_EXCEEDS_THRESHOLD_MESSAGE", new Object[] {sr.getName(), THRESHOLD}, LogEntry.Level.WARNING);
                    LogKeeper.log(le);
                }
            }
        } catch (BadAPICallException ex) {
            Logger.getLogger(getClass()).error("Failed to retrieve SRs", ex);
        }
    }

    /**
     * Gets SR usage percentage
     * @param sr the SR
     * @param includeVirtual return virtual allocation or physical allocation
     * @return percentage
     */
    public double getUsage(SR sr, boolean includeVirtual) {
        if (!sr.getContentType().equals("user")) {
            return 0;
        }

        return ((includeVirtual ? sr.getVirtualAllocation() : sr.getPhysicalUtilisation()) / (double) sr.getPhysicalSize()) * 100;
    }

    /**
     * Gets SR free space in bytes
     * @param sr the SR
     * @return number of bytes
     */
    public long getFreeSpace(SR sr) {
        if (!sr.getContentType().equals("user")) {
            return 0;
        }

        return sr.getPhysicalSize() - sr.getPhysicalUtilisation();
    }
}
