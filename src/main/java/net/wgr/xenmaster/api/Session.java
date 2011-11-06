/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.controller.Controller;
import net.wgr.xenmaster.controller.Dispatcher;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 2, 2011
 * @author double-u
 */
public class Session extends XenApiEntity {

    protected String thisHost, thisUser;
    protected boolean proxy;

    public Session() {
        super(null);
    }

    public Session(String ref) {
        super(ref);
    }

    public Session(String ref, boolean autoFill) {
        super(ref, autoFill);
        if (ref != null && ref.isEmpty()) proxy = true;
    }

    public void loginWithPassword(String userName, String password) {
        try {
            this.reference = (String) Dispatcher.get().dispatch("session.login_with_password", new Object[]{userName, password});
            fillOut();
        } catch (BadAPICallException ex) {
            Logger.getLogger(Session.class).error("Failed to log in", ex);
        }
    }

    public void fill() {
        this.fillOut();
    }

    public Host getThisHost() {
        if (proxy) {
            return Controller.getSession().getThisHost();
        }
        thisHost = value(thisHost, "get_this_host");
        return new Host(thisHost);
    }
}
