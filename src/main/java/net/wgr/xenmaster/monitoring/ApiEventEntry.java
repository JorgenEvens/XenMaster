/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import net.wgr.core.dao.TypeOverride;
import net.wgr.xenmaster.api.XenApiEntity;

/**
 * 
 * @created Jan 22, 2012
 * @author double-u
 */
public class ApiEventEntry extends LogEntry {

    @TypeOverride(type = "BytesType")
    protected XenApiEntity subject;
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
    public ApiEventEntry(String reference, String entityType, String title, String message, XenApiEntity subject, String operation, Level level) {
        super(reference, entityType, title, message, level);
        
        this.subject = subject;
        this.date = System.currentTimeMillis();
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public XenApiEntity getSubject() {
        return subject;
    } 
}
