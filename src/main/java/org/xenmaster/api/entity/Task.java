/*
 * Task.java
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
package org.xenmaster.api.entities;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.xenmaster.controller.BadAPICallException;

/**
 * 
 * @created Dec 14, 2011
 * @author double-u
 */
public class Task extends NamedEntity {

    protected Date created, finished;
    protected String residentOn;
    protected double progress;
    protected String result;
    protected Status status;
    protected List<String> errorInfo;
    protected List<String> subtasks;
    protected String type;
    protected boolean forwarded;
    @Fill
    protected Map<String, String> otherConfig;
    
    public Task() {
        
    }

    public Task(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public Task(String ref) {
        super(ref);
    }

    public Date getCreated() {
        return created;
    }

    public List<String> getErrorInfo() {
        return errorInfo;
    }

    public Date getFinished() {
        return finished;
    }

    public double getProgress() {
        return progress;
    }

    public String getResidentOn() {
        return residentOn;
    }

    public String getResult() {
        return result;
    }

    public static List<Task> getAll() throws BadAPICallException {
        return getAllEntities(Task.class);
    }
    
    public static enum Status {
         /**
         * The value does not belong to this enumeration
         */
        UNRECOGNIZED,
        /**
         * task is in progress
         */
        PENDING,
        /**
         * task was completed successfully
         */
        SUCCESS,
        /**
         * task has failed
         */
        FAILURE,
        /**
         * task is being cancelled
         */
        CANCELLING,
        /**
         * task has been cancelled
         */
        CANCELLED
    }
}
