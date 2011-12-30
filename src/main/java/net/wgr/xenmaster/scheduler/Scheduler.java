/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.scheduler;

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
