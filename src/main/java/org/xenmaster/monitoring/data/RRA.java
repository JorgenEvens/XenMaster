/*
 * RRA.java
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
package org.xenmaster.monitoring.data;

import com.thoughtworks.xstream.XStream;
import java.util.List;

/**
 * 
 * @created Nov 6, 2011
 * @author double-u
 */
public class RRA {
    protected String cf;
    protected List<List<Double>> database;
    
    public static XStream setupForRRAParsing(XStream xs) {
        xs.addImplicitCollection(RRD.class, "rra", RRA.class);
        xs.omitField(RRA.class, "params");
        xs.omitField(RRA.class, "cdp_prep");
        xs.omitField(RRA.class, "pdp_per_row");
        xs.addImplicitArray(RRA.class, "database", List.class);
        xs.aliasType("row", List.class);
        xs.aliasType("v", Double.class);
        return xs;
    }
}
