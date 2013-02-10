/*
 * VBDMetrics.java
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
package org.xenmaster.api.entity;

import java.util.Date;

/**
 * 
 * @created Oct 20, 2011
 * @author double-u
 */
public class VBDMetrics extends XenApiEntity {
    
    protected float readKbs, writeKbs;
    protected Date lastUpdated;

    public VBDMetrics(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public VBDMetrics(String ref) {
        super(ref);
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    // TODO rename
    public float getReadKbs() {
        return readKbs;
    }

    public float getWriteKbs() {
        return writeKbs;
    }
}
