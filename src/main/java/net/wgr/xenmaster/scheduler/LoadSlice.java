/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.scheduler;

/**
 * 
 * @created Sep 29, 2011
 * @author double-u
 */
public class LoadSlice {
    protected double load;
    protected int period;

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    /**
     * In seconds, mind you
     * @return 
     */
    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
