/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.wgr.xenmaster.controller.BadAPICallException;
import net.wgr.xenmaster.controller.Controller;

/**
 * 
 * @created Dec 9, 2011
 * @author double-u
 */
public class Event extends XenApiEntity {

    protected int id;
    protected Date timestamp;
    protected String eventClass;
    protected UUID subject;
    protected String operation;
    protected static int connectionIndex;

    public Event() {
    }

    public Event(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public Event(String ref) {
        super(ref);
    }

    public static void register(List<String> eventClasses) throws BadAPICallException {
        if (eventClasses == null) {
            eventClasses = new ArrayList<>();
        }
        Controller.dispatch("event.register", eventClasses);
    }

    public static void register() throws BadAPICallException {
        ArrayList<String> eventClasses = new ArrayList<>();
        eventClasses.add("*");
        Controller.dispatch("event.register", eventClasses);
    }

    public static void unregister(List<String> eventClasses) throws BadAPICallException {
        if (eventClasses == null) {
            eventClasses = new ArrayList<>();
        }
        Controller.dispatch("event.unregister", eventClasses);
    }

    public static List<Event> nextEvents() throws BadAPICallException {
        if (connectionIndex == 0) connectionIndex = Controller.getLocal().getDispatcher().getConnections().requestNewConnection();
        ArrayList<Event> events = new ArrayList<>();
        Object obj = Controller.dispatchOn("event.next", connectionIndex);
        Object[] result = (Object[]) obj;
        if (result == null) {
            return events;
        }
        for (Object o : result) {
            Event event = new Event();
            event.fillOut((Map<String, Object>) o);
            events.add(event);
        }
        return events;
    }

    public String getEventClass() {
        return eventClass;
    }

    public void setEventClass(String eventClass) {
        this.eventClass = eventClass;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public UUID getSubject() {
        return subject;
    }

    public void setSubject(UUID subject) {
        this.subject = subject;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("eventClass", "class");
        map.put("subject", "obj_uuid");
        return map;
    }
}