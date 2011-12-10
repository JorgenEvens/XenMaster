/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import com.lmax.disruptor.EventHandler;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;
import net.wgr.xenmaster.connectivity.Connections;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 14, 2011
 * @author double-u
 */
public class Collector implements EventHandler<Record> {

    protected long lastUpdate;
    protected Connections conn;
    protected static ConcurrentHashMap<String, URLConnection> connections = new ConcurrentHashMap<>();

    public Collector(Connections conn) {
        this.conn = conn;
    }

    protected void connect(String reference) {
        try {
            URL url = new URL(conn.getUrl().toExternalForm() + "/host_rrd");
            URLConnection uc = url.openConnection();
            uc.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64("root:r00tme".getBytes())));
            uc.connect();
        } catch (Exception ex) {
            Logger.getLogger(getClass()).error("Failed to retrieve statistics", ex);
        }
    }

    @Override
    public void onEvent(Record t, long l, boolean bln) throws Exception {
        if (!t.isVM()) {
            if (!connections.containsKey(t.getReference())) {
                connect(t.getReference());
            }
            
            URLConnection uc = connections.get(t.getReference());
            t.setXML(IOUtils.toString(uc.getInputStream()));
        }
    }

}
