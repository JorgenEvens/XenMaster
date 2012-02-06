/*
 * Host.java
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
package org.xenmaster.entities;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;

import net.wgr.core.dao.Required;
import net.wgr.core.data.Retrieval;

import org.apache.log4j.Logger;
import org.xenmaster.connectivity.Connections;

/**
 * 
 * @created Oct 8, 2011
 * @author double-u
 */
public class Host extends net.wgr.core.dao.Object {

    @Required
    protected UUID id;
    protected String macAddress;
    @Required
    protected InetAddress address;
    protected int port = 80;
    protected String userName, password;
    protected boolean useSSL, active;
    
    public final static String COLUMN_FAMILY = "xenHosts";

    public Host(InetAddress address) {
        this.address = address;
    }

    @Override
    public String getColumnFamily() {
        return COLUMN_FAMILY;
    }

    @Override
    public String getKeyFieldName() {
        return "id";
    }

    public UUID getId() {
        return id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean usesSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Connections connect() {
        try {
            URL url = new URL("http://" + address.getCanonicalHostName() + ":" + port);
            return new Connections(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass()).error(ex);
        }
        return null;
    }
    
    public static Collection<Host> getAll() {
        Collection<Host> hosts;
        hosts = Retrieval.getRowsAs(Host.class, Retrieval.getAllRowsFromColumnFamily(COLUMN_FAMILY)).values();
        return hosts;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
