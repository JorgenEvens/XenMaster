/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.scheduler;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.Period;

/**
 * The oracle is all-knowing
 * @created Sep 29, 2011
 * @author double-u
 */
public class Oracle {
    
    protected TimeSpan timeSpan;
    
    public List<LoadSlice> predict(Period p) {
        ArrayList<LoadSlice> slices = new ArrayList<LoadSlice>();
        return slices;
    }
    
    public static enum TimeSpan {
        DAY, WEEK
    }
}
