/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.connectivity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.wgr.xenmaster.api.Session;

/**
 * 
 * @created Oct 2, 2011
 * @author double-u
 */
public class Connections {

    protected final List<XMLRPC> xmlRpcs;
    protected final Session session = new Session();
    protected URL url;

    public Connections(URL url) {
        this.url = url;
        this.xmlRpcs = new ArrayList<>();
        this.xmlRpcs.add(new XMLRPC(url));
    }
    
    public Map executeCommand(String commandName, List params, int connection) {
        return xmlRpcs.get(connection).execute(commandName, params);
    }

    public int requestNewConnection() {
        synchronized (xmlRpcs) {
            this.xmlRpcs.add(new XMLRPC(url));
            return this.xmlRpcs.size() - 1;
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
