/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.controller.Controller;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 2, 2011
 * @author double-u
 */
public class Session extends XenApiEntity {

    protected String thisHost, thisUser;
    
    public Session() {
        super(null);
    }

    public Session(String ref) {
        super(ref);
    }

    public Session(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public void loginWithPassword(String userName, String password) {
        try {
            this.reference = (String) Controller.getLocal().getDispatcher().dispatch("session.login_with_password", new Object[]{userName, password});
            fillOut();
        } catch (BadAPICallException ex) {
            Logger.getLogger(Session.class).error("Failed to log in", ex);
        }
    }

    public void fill() {
        this.fillOut();
    }

    public Host getThisHost() {
        if (thisHost == null) {
            if (Controller.getSession() == null) {
                throw new IllegalStateException("Not authenticated");
            } else {
                return Controller.getSession().getThisHost();
            }
        }
        return new Host(thisHost);
    }
}
