/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.wgr.xenmaster.api.Event;
import net.wgr.xenmaster.controller.BadAPICallException;
import org.apache.log4j.Logger;

/**
 * 
 * @created Jan 4, 2012
 * @author double-u
 */
public class EventHandler implements Runnable {
    
    protected boolean run;
    protected Thread thread;
    protected List<EventListener> listeners;
    
    public EventHandler() {
        this.listeners = new CopyOnWriteArrayList<>();
        this.thread = new Thread(this);
        this.thread.setName("EventHandler");
    }
    
    public void addListener(EventListener el) {
        this.listeners.add(el);
    }
    
    public void stopListener(EventListener el) {
        this.listeners.remove(el);
    }
    
    public void start() {
        run = true;
        thread.start();
    }
    
    public void stop() {
        run = false;
    }
    
    @Override
    public void run() {
        try {
            // todo this should always take place on pool masters, explicitly set context
            Event.register();
        } catch (BadAPICallException ex) {
            Logger.getLogger(getClass()).error("Failed to register to events", ex);
        }
        
        while (run) {
            try {
                List<Event> latest = Event.nextEvents();
                for (Event event : latest) {
                    for (EventListener el : listeners) {
                        el.eventOcurred(event);
                    }
                }
            } catch (BadAPICallException ex) {
                run = false;
                Logger.getLogger(getClass()).error("Failed to retrieve latest events", ex);
            }
        }
    }
    
    public static interface EventListener {
        
        public void eventOcurred(Event event);
    }
}
