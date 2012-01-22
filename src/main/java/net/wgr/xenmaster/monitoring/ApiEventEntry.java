/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import java.util.Date;
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

    public ApiEventEntry(String entityType, String title, String message, XenApiEntity subject, String operation, Level level) {
        super(subject.getReference(), entityType, title, message, level);
        
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
