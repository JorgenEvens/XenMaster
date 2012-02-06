/*
 * Preflight.java
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
package org.xenmaster.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xenmaster.entities.Host;
import org.xenmaster.connectivity.Connections;

/**
 * 
 * @created Oct 13, 2011
 * @author double-u
 */
public class Preflight {
    
    public static Map<Host, Connections> checkHosts(Set<Host> hosts) {
        HashMap<Host, Connections> conns = new HashMap<>();
        for (Host host : hosts) {
            try {
                Connections c = host.connect();
                if (c != null) {
                    conns.put(host, c);
                } else {
                    if (host.getAddress().isReachable(500)) {
                        Logger.getLogger(Preflight.class).warn("Host " + host.getAddress().getCanonicalHostName() + " is up, but XAPI is not responding. Check your configuration.");
                    } else {
                        Logger.getLogger(Preflight.class).warn("Failed to connect to " + host.getAddress().getCanonicalHostName());
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(Preflight.class).error(ex);
            }
        }
        return conns;
    }
}
