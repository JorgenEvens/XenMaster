/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.wgr.core.ReflectionUtils;
import net.wgr.lang.I18N;
import net.wgr.wcp.Commander;
import net.wgr.wcp.Scope;
import net.wgr.wcp.command.Command;
import net.wgr.xenmaster.api.Event;
import net.wgr.xenmaster.api.NamedEntity;
import net.wgr.xenmaster.api.Task;
import net.wgr.xenmaster.api.util.CachingFacility;
import org.apache.log4j.Logger;

/**
 * Issues requested monitoring data "formally and with authority"
 * @created Oct 30, 2011
 * @author double-u
 */
public class Emitter {

    protected List<EventDescriptor> descriptors;

    public Emitter() {
        descriptors = new ArrayList<>();
        buildDescriptors();
    }

    protected final void buildDescriptors() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/trans_" + I18N.instance().getDefaultLocale().getLanguage() + ".events")))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) continue;
                descriptors.add(EventDescriptor.fromString(line));
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass()).error("Failed to read event file", ex);
        }
    }

    public void listenToEvents(EventHandler eh) {
        eh.addListener(new EventHandler.EventListener() {

            @Override
            public void eventOcurred(Event event) {
                if (event.getSnapshot() == null) {
                    return;
                }
                boolean isDeletion = event.getOperation() == Event.Operation.DEL;
                Map<String, Object> diff = ReflectionUtils.diff(CachingFacility.get(event.getSnapshot().getReference(!isDeletion), event.getSnapshot().getClass()), event.getSnapshot());
                if (diff.size() < 1) {
                    // Nothing was changed?
                    return;
                }
                
                String title = event.getOperation().name();
                String message = "";
                
                if (!isDeletion && !Task.class.isAssignableFrom(event.getSnapshot().getClass())) {
                    diffIt:
                    for (Map.Entry<String, Object> d : diff.entrySet()) {
                        for (EventDescriptor ed : descriptors) {
                            if (ed.match(event.getSnapshot().getClass().getSimpleName(), d.getKey(), d.getValue().toString())) {
                                title = ed.getTitle();
                                message = ed.getDescription();
                                
                                if (NamedEntity.class.isAssignableFrom(event.getSnapshot().getClass())) {
                                    NamedEntity ne = (NamedEntity) event.getSnapshot();
                                    title = String.format(title, ne.getName(), ne.getDescription());
                                    message = String.format(message, ne.getName(), ne.getDescription());
                                }
                                break diffIt;
                            }
                        }
                    } 
                }
                
                ApiEventEntry le = new ApiEventEntry(event.getEventClass(), title, message, event.getSnapshot(), event.getOperation().name(), LogEntry.Level.INFORMATION);
                emit(le);
            }
        }, 0);
    }

    public static void emit(LogEntry le) {
        Commander.getInstance().commandeer(new Command("log", "event", le), new Scope(Scope.Target.ALL));
    }

    protected static final class EventDescriptor {

        protected String className;
        protected String field;
        protected String newValue;
        protected String title, description;
        protected final static Pattern EVENT_DESCRIPTOR_LINE = Pattern.compile("([^\\.]+)\\.([^:]+):([^=]+)=([^;]+);(.+)");

        public EventDescriptor(String className, String field, String newValue, String title, String description) {
            this.className = className;
            this.field = field;
            this.newValue = newValue;
            this.title = title;
            this.description = description;
        }

        public boolean match(String className, String fieldName, String newValue) {
            return this.className.equals(className) && this.field.equals(fieldName) && this.newValue.equals(newValue);
        }

        public static EventDescriptor fromString(String string) {
            Matcher m = EVENT_DESCRIPTOR_LINE.matcher(string);
            if (!m.find()) {
                return null;
            }

            EventDescriptor ed = new EventDescriptor(m.group(1).trim(), m.group(2).trim(), m.group(3).trim(), m.group(4).trim(), m.group(5).trim());
            return ed;
        }

        public String getClassName() {
            return className;
        }

        public String getField() {
            return field;
        }

        public String getNewValue() {
            return newValue;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }
}
