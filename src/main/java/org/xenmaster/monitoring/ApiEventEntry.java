/*
 * ApiEventEntry.java
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

import java.util.Map;

import net.wgr.core.dao.TypeOverride;

/**
 * 
 * @created Jan 22, 2012
 * @author double-u
 */
public class ApiEventEntry extends LogEntry {

    @TypeOverride(type = "BytesType")
    protected Map<String, Object> changes;
    protected String operation;

    /**
     * ApiEventEntry ctor
     * @param reference cannot be retrieved from the XenApiEntity as it might not be available
     * @param entityType entity class name
     * @param title error title
     * @param message error message
     * @param subject entity who caused event
     * @param operation ADD, MOD, DEL
     * @param level event level
     */
    public ApiEventEntry(String reference, String entityType, String title, String message, Map<String, Object> subject, String operation, Level level) {
        super(reference, entityType, title, message, level);
        
        this.changes = subject;
        this.date = System.currentTimeMillis();
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public Map<String, Object> getChanges() {
        return changes;
    }
}
