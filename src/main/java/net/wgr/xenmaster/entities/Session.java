/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.entities;

import java.util.UUID;
import net.wgr.xenmaster.controller.Controller;

/**
 * 
 * @created Oct 2, 2011
 * @author double-u
 */
public class Session extends XenApiEntity {
    
    protected String thisHost, thisUser;

    public Session(String ref) {
        super(ref);
        fillOut();
    }

    public static Session loginWithPassword(String userName, String password) {
        String ref = (String) Controller.dispatch("session.login_with_password", userName, password);
        return new Session(ref);
    }
    
    public Host getThisHost() {
        return new Host(thisHost);
    }
}
