/*
 * EventHandler.java
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
package org.xenmaster.monitoring;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.xenmaster.api.entities.Event;
import org.xenmaster.controller.BadAPICallException;

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
    
    public void addListener(EventListener el, int index) {
        this.listeners.add(index, el);
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
