/*
 * LogEntry.java
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

import java.util.Arrays;

import net.wgr.core.dao.AutoGenerated;
import net.wgr.core.dao.TypeOverride;
import net.wgr.lang.I18N;

/**
 * 
 * @created Oct 23, 2011
 * @author double-u
 */
public class LogEntry extends net.wgr.core.dao.Object {

    @AutoGenerated(strategy = AutoGenerated.GenerationStrategy.SEQUENTIAL)
    protected int id;
    protected String reference;
    protected String entityType;
    protected long date;
    protected String title, message;
    protected Level level;
    @TypeOverride(type = "BytesType")
    protected Object[] args;

    public LogEntry(String reference, String entityType, String title, String message, Level level) {
        this(reference, entityType, title, message, null, level);
    }

    public LogEntry(String reference, String entityType, String title, String message, Object[] args, Level level) {
        this.reference = reference;
        this.entityType = entityType;
        this.title = title;
        this.message = message;
        this.level = level;
        this.date = System.currentTimeMillis();
        this.args = (args != null ? Arrays.copyOf(args, args.length) : null);
    }

    public LogEntry(String reference, Class entityType, String title, Level level) {
        this(reference, entityType, title, null, level);
    }

    public LogEntry(String reference, Class entityType, String title, Object[] args, Level level) {
        this.reference = reference;
        this.entityType = entityType.getSimpleName();
        this.title = title;
        this.message = title + "_MESSAGE";
        this.level = level;
        this.args = Arrays.copyOf(args, args.length);
        this.date = System.currentTimeMillis();
    }

    public String getEntityType() {
        return entityType;
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        if (message == null) {
            return getTitle();
        }
        
        if (args != null && I18N.hasText(message)) {
            return String.format(I18N.getText(message), args);
        }
        return message;
    }

    public String getReference() {
        return reference;
    }

    public String getTitle() {
        if (args != null) {
            return String.format(I18N.getText(title), args);
        }
        return title;
    }

    @Override
    public String getColumnFamily() {
        return "logs";
    }

    @Override
    public String getKeyFieldName() {
        return "id";
    }

    public void doLocalization() {
        this.title = getTitle();
        this.message = getMessage();
    }

    @Override
    public void delete() {
        throw new IllegalAccessError("Only a LogKeeper can remove a LogEntry");
    }

    @Override
    public void insert() {
        throw new IllegalAccessError("Only a LogKeeper can insert a LogEntry");
    }

    public void insert(LogKeeper keeper) {
        super.insert();
    }

    protected void delete(LogKeeper keeper) {
        super.delete();
    }

    public static enum Level {

        ERROR, WARNING, SUCCESS, INFORMATION, DETAIL;

        public org.apache.log4j.Level toLog4jLevel() {
            switch (this) {
                case DETAIL:
                    return org.apache.log4j.Level.DEBUG;
                case INFORMATION:
                    return org.apache.log4j.Level.INFO;
                case WARNING:
                    return org.apache.log4j.Level.WARN;
                case ERROR:
                    return org.apache.log4j.Level.ERROR;
                default:
                    return org.apache.log4j.Level.FATAL;
            }
        }
    }
}