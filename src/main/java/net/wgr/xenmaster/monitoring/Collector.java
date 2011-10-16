/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import java.net.URL;
import java.net.URLConnection;
import net.wgr.xenmaster.connectivity.Connection;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 14, 2011
 * @author double-u
 */
public class Collector implements Runnable {

    protected long lastUpdate;
    protected Connection conn;

    public Collector(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(conn.getUrl().toExternalForm() + "/host_rrd");
            URLConnection uc = url.openConnection();
            uc.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64("root:test".getBytes())));
            uc.connect();
            
            
            
        } catch (Exception ex) {
            Logger.getLogger(getClass()).error("Failed to retrieve statistics", ex);
        }
    }

}
