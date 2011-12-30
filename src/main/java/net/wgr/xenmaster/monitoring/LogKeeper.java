/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 23, 2011
 * @author double-u
 */
public class LogKeeper {

    protected LinkedList<LogEntry> backlog;
    protected boolean isMaster;
    private static LogKeeper instance;

    public static LogKeeper get() {
        if (instance == null) {
            instance = new LogKeeper();
        }
        return instance;
    }

    private LogKeeper() {
        backlog = new LinkedList<>();
    }

    public static void log(LogEntry le) {
        get().write(le);
    }

    public void write(LogEntry le) {
        backlog.add(le);
        try {
            Emitter.emit(le);
            le.insert(this);
            backlog.remove(le);
        } catch (Exception ex) {
            Logger.getLogger(getClass()).error("LogKeeper failed to write log entry", ex);
        }
    }

    public void becomeMaster() {
        // The Force is strong
        // todo Check if there aren't any other masters still alive
        isMaster = true;
    }
}
