/*
 * XMLRPC.java
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    protected final XmlRpcClient client;
    protected final Logger logger = Logger.getLogger(getClass());
    public static final int CONNECTION_TIMEOUT = 300;

    public XMLRPC(URL host) {
        cfg = new XmlRpcClientConfigImpl();
        cfg.setServerURL(host);
        cfg.setConnectionTimeout(CONNECTION_TIMEOUT);
        client = new XmlRpcClient();
        client.setConfig(cfg);
    }

    public Map execute(String method, List params) {
        try {
            synchronized (client) {
                return (Map) client.execute(method, prepareForTransport(params));
            }
        } catch (XmlRpcException ex) {
            logger.info("Call failed", ex);
        }
        return null;
    }

    protected List prepareForTransport(List params) {
        ArrayList safeList = new ArrayList(params.size());
        for (Object obj : params) {
            if (obj instanceof List) {
                safeList.add(prepareForTransport((List) obj));
            } else if (obj instanceof Map) {
                Map source = (Map) obj;
                List safe = prepareForTransport(new ArrayList(source.values()));
                Iterator safeListIterator = safe.iterator();
                HashMap safeMap = new HashMap(source.size());
                for (Iterator<Entry> it = source.keySet().iterator(); it.hasNext() || safeListIterator.hasNext();) {
                    safeMap.put(it.next(), safeListIterator.next());
                }
                safeList.add(safeMap);
            } else {
                safeList.add(castToSafeType(obj));
            }
        }
        
        return safeList;
    }

    protected Object castToSafeType(Object source) {
        if (source instanceof Long) {
            return Long.toString((long) source);
        }
        return source;
    }
}
