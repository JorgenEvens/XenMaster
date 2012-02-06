/*
 * Connections.java
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
package org.xenmaster.connectivity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.xenmaster.api.Session;

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
