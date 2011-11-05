/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.controller;

import net.wgr.xenmaster.api.Session;

/**
 * Provides easily accessible context per thread
 * @created Oct 1, 2011
 * @author double-u
 */
public class Controller extends ThreadLocal<Controller> {

    private static ThreadLocal<Controller> instance;
    protected Dispatcher dispatcher;

    private Controller() {
        this.dispatcher = new Dispatcher();
    }

    public static Controller getLocal() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance.get();
    }

    @Override
    protected Controller initialValue() {
        return new Controller();
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }
    
    public static Session getSession() {
        return getLocal().getDispatcher().getConnection().getSession();
    }
    
    public static Object dispatch(String methodName, Object ... params) throws BadAPICallException {
        return getLocal().getDispatcher().dispatchWithSession(methodName, params);
    }
}
