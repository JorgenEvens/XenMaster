/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.ArrayList;
import java.util.List;
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
    protected boolean proxy;
    protected List<SessionActivityListener> listeners;

    public Session() {
        this(null);
    }

    public Session(String ref) {
        super(ref);
        this.listeners = new ArrayList<>();
    }

    public Session(String ref, boolean autoFill) {
        super(ref, autoFill);
        if (ref != null && ref.isEmpty()) proxy = true;
    }
    
    public void addListener(SessionActivityListener sal) {
        this.listeners.add(sal);
    }

    public void loginWithPassword(String userName, String password) {
        try {
            this.reference = (String) Controller.getLocal().getDispatcher().dispatch("session.login_with_password", new Object[]{userName, password});
            fillOut();
            for (SessionActivityListener sel : listeners) {
                sel.sessionEstablished(this);
            }
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
    
    public static interface SessionActivityListener {
        public void sessionEstablished(Session session);
    }
}
