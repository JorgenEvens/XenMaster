/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.connectivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import net.wgr.settings.Settings;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * 
 * @created Oct 2, 2011
 * @author double-u
 */
public class XMLRPC {
    protected XmlRpcClientConfigImpl cfg;
    protected XmlRpcClient client;
    
    public XMLRPC() throws MalformedURLException {
        cfg = new XmlRpcClientConfigImpl();
        cfg.setServerURL(new URL(Settings.getInstance().getString("Xen.URL")));
        client = new XmlRpcClient();
        client.setConfig(cfg);
    }
    
    public Map execute(String method, List params) {
        try {
            return (Map) client.execute(method, params);
        } catch (XmlRpcException ex) {
            Logger.getLogger(getClass()).error(ex);
        }
        return null;
    }
}
