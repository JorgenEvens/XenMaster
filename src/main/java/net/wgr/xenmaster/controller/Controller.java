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

    public static Session getSession() {
        return Dispatcher.get().getConnection().getSession();
    }
    
    public static Object dispatch(String methodName, Object ... params) throws BadAPICallException {
        return Dispatcher.get().dispatchWithSession(methodName, params);
    }
}
