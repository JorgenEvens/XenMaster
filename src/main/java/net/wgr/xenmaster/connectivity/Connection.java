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
import net.wgr.xenmaster.api.Session;

/**
 * 
 * @created Oct 2, 2011
 * @author double-u
 */
public class Connection extends ThreadLocal {

    protected XMLRPC xmlRpc;
    protected Session session;
    protected URL url;

    public Connection() throws MalformedURLException {
        this(new URL(Settings.getInstance().getString("Xen.URL")));
    }

    public Connection(URL url) {
        this.url = url;
        this.xmlRpc = new XMLRPC(url);
    }

    public Map executeCommand(String commandName, List params) {
        return xmlRpc.execute(commandName, params);
    }

    public Session getSession() {
        if (session == null) {
            authenticate();
            if (session == null) {
                throw new Error("Failed to setup the connection. Check if XAPI is running and configuration is correct");
            }
        }
        return session;
    }

    public URL getUrl() {
        return url;
    }

    public void authenticate() {
        // TODO
        session = Session.loginWithPassword("root", "test");
        if (session != null) {
            session.fill();
        }
    }
}
