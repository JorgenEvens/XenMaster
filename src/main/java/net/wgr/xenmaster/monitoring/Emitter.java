/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import java.util.Map;
import net.wgr.core.ReflectionUtils;
import net.wgr.wcp.Commander;
import net.wgr.wcp.Scope;
import net.wgr.wcp.command.Command;
import net.wgr.xenmaster.api.Event;
import net.wgr.xenmaster.api.util.CachingFacility;

/**
 * Issues requested monitoring data "formally and with authority"
 * @created Oct 30, 2011
 * @author double-u
 */
public class Emitter {
    
    public void Emitter() {
    }
    
    public void listenToEvents(EventHandler eh) {
        eh.addListener(new EventHandler.EventListener() {
            
            @Override
            public void eventOcurred(Event event) {
                if (event.getSnapshot() == null) return;
                Map<String, Object> diff = ReflectionUtils.diff(CachingFacility.get(event.getSnapshot().getReference(), event.getSnapshot().getClass()), event.getSnapshot());
                if (diff.size() < 1) {
                    // Nothing was changed?
                    return;
                }
                LogEntry le = new LogEntry(event.getReference(), event.getEventClass(), event.getOperation(), diff, event.getTimestamp(), LogEntry.Level.INFORMATION);
                emit(le);
            }
        });
    }
    
    public static void emit(LogEntry le) {
        Commander.getInstance().commandeer(new Command("log", "event", le), new Scope(Scope.Target.ALL));
    }
}
