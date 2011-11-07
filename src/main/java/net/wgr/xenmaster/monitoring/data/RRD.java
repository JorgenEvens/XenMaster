/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring.data;

import com.thoughtworks.xstream.XStream;
import java.io.InputStream;
import java.util.List;

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
    
    public static RRD parse(InputStream is) {
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
        RRD fromXML = (RRD) xs.fromXML(is);
        
        return fromXML;
    }
}
