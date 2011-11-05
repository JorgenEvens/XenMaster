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
public class Connection {

    protected final XMLRPC xmlRpc;
    protected final Session session = new Session();
    protected URL url;

    public Connection() throws MalformedURLException {
        this(new URL(Settings.getInstance().getString("Xen.URL")));
    }

    public Connection(URL url) {
        this.url = url;
        this.xmlRpc = new XMLRPC(url);
    }

    public Map executeCommand(String commandName, List params) {
        synchronized (xmlRpc) {
            return xmlRpc.execute(commandName, params);
        }
    }

    public Session getSession() {
        return session;
    }

    public URL getUrl() {
        return url;
    }

    public void authenticate(String userName, String password) {
        synchronized (session) {
            session.loginWithPassword(userName, password);
            if (session.getReference() != null) {
                session.fill();
            }
        }
    }
}
