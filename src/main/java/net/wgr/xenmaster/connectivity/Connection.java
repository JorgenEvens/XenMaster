/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.connectivity;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import net.wgr.xenmaster.entities.Session;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 2, 2011
 * @author double-u
 */
public class Connection {
    protected XMLRPC x;
    protected Session s;
    
    public Connection() {
        try {
            this.x = new XMLRPC();
        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass()).error(ex);
        }
    }
    
    public Map executeCommand(String commandName, List params) {
        return x.execute(commandName, params);
    }

    public Session getSession() {
        if (s == null) authenticate();
        return s;
    }
    
    public void authenticate() {
        // TODO
        s = Session.loginWithPassword("", "");
        s.fill();
    }
}
