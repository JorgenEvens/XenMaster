/*
 * LogKeeper.java
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
