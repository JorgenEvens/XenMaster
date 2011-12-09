/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.controller;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.wgr.xenmaster.api.Session;
import net.wgr.xenmaster.entities.Host;
import org.apache.log4j.Logger;

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
        d.getConnection().getSession().addListener(new SAL());
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
        if (instance == null) {
            Logger.getLogger(Controller.class).warn("Controller is not initialized");
            return null;
        }
        return instance.get();
    }

    public static void switchContext(UUID hostUUID) {
        if (!instances.containsKey(hostUUID)) {
            // new Controller(getConfigForHost(uuid))
            //Controller c = new Controller();
            //c.getDispatcher().getConnection().getSession().loginWithPassword("root", "r00tme");
        }
        instance.set(instances.get(hostUUID));
    }

    public static void switchToNextAvailableContext() {
        if (instances.size() < 1) {
            Logger.getLogger(Controller.class).error("No host context available");
        } else {
            instance.set(instances.values().iterator().next());
        }
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public static Session getSession() {
        return Controller.getLocal().getDispatcher().getConnection().getSession();
    }

    public static Object dispatch(String methodName, Object... params) throws BadAPICallException {
        if (Controller.getLocal() == null) {
            Logger.getLogger(Controller.class).warn("Local Host context not set, switching to next available one");
            switchToNextAvailableContext();
        }
        return Controller.getLocal().getDispatcher().dispatchWithSession(methodName, params);
    }

    protected class SAL implements Session.SessionActivityListener {

        @Override
        public void sessionEstablished(Session session) {
            instances.put(session.getThisHost().getUUID(), Controller.this);
        }
    }
}
