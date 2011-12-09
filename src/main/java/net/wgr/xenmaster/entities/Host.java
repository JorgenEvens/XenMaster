/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.entities;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;
import net.wgr.core.dao.Required;
import net.wgr.core.data.Retrieval;
import net.wgr.xenmaster.connectivity.Connection;
import org.apache.log4j.Logger;

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

    public Connection connect() {
        try {
            URL url = new URL("http://" + address.getCanonicalHostName() + ":" + port);
            return new Connection(url);
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
