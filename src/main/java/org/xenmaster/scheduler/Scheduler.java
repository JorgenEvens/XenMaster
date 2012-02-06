/*
 * Scheduler.java
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
package org.xenmaster.scheduler;

import org.apache.log4j.Logger;

/**
 * 
 * @created Sep 29, 2011
 * @author double-u
 */
public class Scheduler {

    protected double currentCapacity;
    protected double trainingFactor = 1;
    
    // Can be e.g. 31 - [day of training month]
    protected double fudgeFactor = 1;

    protected LoadSlice scheduleNextSlice() {
        return null;
    }

    protected void parseSlice(LoadSlice expected, LoadSlice real) {
        double deviation = real.getLoad() - expected.getLoad();

        /*if (deviation < -75) {
            // Quiet day huh?
        } else if (deviation < -50) {
            // Still way off
        } else if (deviation < -25) {
            // Time to get the load back baby
        } else if (deviation < 25) {
            // We're cool, correct prediction
        } else if (deviation < 50) {
            // Inaccurate prediction
        } else if (deviation < 75) {
            // The oracle has been drinking again
        } else {
            // Uh-oh
            Logger.getLogger(getClass()).warn("The real load has deviated with +75% or more from the predicted load");
        }*/
        
        // Lots of deviation = higher trainingFactor
        trainingFactor = Math.max(((Math.abs(deviation) / 100) * trainingFactor) * fudgeFactor, 1);
    }

    protected void increaseCapacity(double increase) {
        if (currentCapacity + increase > 95) {
            Logger.getLogger(getClass()).warn("Cloud capacity requested is beyond 95%");
        }
    }
}
