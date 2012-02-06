/*
 * Oracle.java
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
        ArrayList<LoadSlice> slices = new ArrayList<>();
        return slices;
    }
    
    public static enum TimeSpan {
        DAY, WEEK
    }
}
