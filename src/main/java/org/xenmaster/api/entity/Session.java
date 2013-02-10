/*
 * Session.java
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
package org.xenmaster.api.entity;

import org.xenmaster.api.entity.Host;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.xenmaster.controller.BadAPICallException;
import org.xenmaster.controller.Controller;

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
        if (ref != null && ref.isEmpty()) {
            proxy = true;
        }
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
