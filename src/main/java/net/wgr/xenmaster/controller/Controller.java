/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.controller;

/**
 * 
 * @created Oct 1, 2011
 * @author double-u
 */
public class Controller {

    private static Controller instance;
    protected Dispatcher dispatcher;

    private Controller() {
        this.dispatcher = new Dispatcher();
    }

    public static Controller get() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }
    
    public static Object dispatch(String methodName, Object ... params) {
        return get().getDispatcher().dispatch(methodName, params);
    }

}
