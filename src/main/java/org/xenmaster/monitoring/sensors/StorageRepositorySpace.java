/*
 * StorageRepositorySpace.java
 * Copyright (C) 2011,2012 Wannes De Smet
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.xenmaster.monitoring.sensors;

import org.apache.log4j.Logger;
import org.joda.time.Period;
import org.xenmaster.api.SR;
import org.xenmaster.controller.BadAPICallException;
import org.xenmaster.monitoring.LogEntry;
import org.xenmaster.monitoring.LogKeeper;
import org.xenmaster.monitoring.Sensor;

/**
 * 
 * @created Jan 17, 2012
 * @author double-u
 */
public class StorageRepositorySpace extends Sensor {
    
    protected final static int THRESHOLD = 95;

    @Override
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
