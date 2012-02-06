/*
 * RRD.java
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

import java.util.List;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @created Oct 14, 2011
 * @author double-u
 */
public class RRD {
    protected int version;
    protected int step;
    
    protected long lastUpdate;
    protected List<DataSet> ds;
    protected List<RRA> rra;
    protected List<List<Double>> database;
    
    public static RRD parse(String xml) {
        XStream xs = new XStream();
        xs.alias("rrd", RRD.class);
        xs.aliasField("lastupdate", RRD.class, "lastUpdate");
        xs.addImplicitCollection(RRD.class, "ds", DataSet.class);
        xs.aliasField("minimal_heartbeat", DataSet.class, "minimalHeartbeat");
        xs.aliasField("last_ds", DataSet.class, "lastDS");
        xs.aliasField("unknown_sec", DataSet.class, "unknownSeconds");
        xs.addImplicitCollection(RRD.class, "rra", RRA.class);
        xs.addImplicitMap(RRA.class, "params", "param", String.class, "param");
        xs.aliasField("pdp_per_row", RRA.class, "pdpPerRow");
        xs.aliasField("cdp_prep", RRA.class, "cdpPrep");
        RRD fromXML = (RRD) xs.fromXML(xml);
        
        return fromXML;
    }
}
