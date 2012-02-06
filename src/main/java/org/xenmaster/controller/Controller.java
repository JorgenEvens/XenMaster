/*
 * Controller.java
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
package org.xenmaster.controller;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.xenmaster.api.Session;
import org.xenmaster.entities.Host;

/**
 * Provides easily accessible context per thread
 * @created Oct 1, 2011
 * @author double-u
 */
public class Controller {

    private static ThreadLocal<Controller> instance = new ThreadLocal<Controller>() {

        @Override
        protected Controller initialValue() {
            return new Controller();
        }
    };
    protected final static ConcurrentHashMap<UUID, Controller> instances = new ConcurrentHashMap<>();
    protected Dispatcher dispatcher;

    private Controller(URL xenHost) {
        this.dispatcher = buildDispatcher(xenHost);
    }

    private Controller() {
    }

    protected final Dispatcher buildDispatcher(URL xenHost) {
        Dispatcher d = new Dispatcher(xenHost);
        d.getConnections().getSession().addListener(new SAL());
        return d;
    }

    public static void build(Host host) {
        ThreadLocal<Controller> tl = new ThreadLocal<>();
        tl.set(new Controller(host.connect().getUrl()));
        instance = tl;
    }

    public static void build(URL url) {
        ThreadLocal<Controller> tl = new ThreadLocal<>();
        tl.set(new Controller(url));
        instance = tl;
    }

    public static Controller getLocal() {
        if (instance.get() == null) {
            Logger.getLogger(Controller.class).warn("Controller is not initialized, switching to next available");
            switchToNextAvailableContext();
        }
        return instance.get();
    }

    public static void switchContext(UUID hostUUID) {
        if (!instances.containsKey(hostUUID)) {
            // new Controller(getConfigForHost(uuid))
            //Controller c = new Controller();
            //c.getDispatcher().getConnection().getSession().loginWithPassword("root", "r00tme");
            Logger.getLogger(Controller.class).info("Creating new context for host " + hostUUID.toString());
        }
        instance.set(instances.get(hostUUID));
    }

    public static void switchToNextAvailableContext() {
        if (instances.size() < 1) {
            Logger.getLogger(Controller.class).error("No host context available");
        } else {
            if (instances.values().iterator().hasNext()) {
                instance.set(instances.values().iterator().next());
            } else {
                Logger.getLogger(Controller.class).info("No more available contexts");
            }
        }
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public static Session getSession() {
        return Controller.getLocal().getDispatcher().getConnections().getSession();
    }

    public static Object dispatch(String methodName, Object... params) throws BadAPICallException {
        if (Controller.getLocal() == null) {
            Logger.getLogger(Controller.class).warn("Local Host context not set, switching to next available one");
            switchToNextAvailableContext();
        }
        return Controller.getLocal().getDispatcher().dispatchWithSession(methodName, params);
    }

    public static Object dispatchOn(String methodName, int connection, Object... params) throws BadAPICallException {
        if (Controller.getLocal() == null) {
            Logger.getLogger(Controller.class).warn("Local Host context not set, switching to next available one");
            switchToNextAvailableContext();
        }
        Logger.getLogger(Controller.class).debug("Dispatching call " + methodName + " to connection #" + connection);
        return Controller.getLocal().getDispatcher().dispatchWithSession(methodName, params, connection);
    }

    protected class SAL implements Session.SessionActivityListener {

        @Override
        public void sessionEstablished(Session session) {
            instances.put(session.getThisHost().getUUID(), Controller.this);
        }
    }
}
