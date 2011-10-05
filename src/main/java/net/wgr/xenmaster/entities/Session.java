/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.entities;

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
    }

    public Session(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public static Session loginWithPassword(String userName, String password) {
        String ref = (String) Controller.get().getDispatcher().dispatch("session.login_with_password", new Object[]{userName, password});
        Session s = new Session(ref, false);
        return s;
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
