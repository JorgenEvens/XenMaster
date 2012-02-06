/*
 * BadAPICallException.java
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
package org.xenmaster.controller;

import java.util.List;

import net.wgr.lang.I18N;

/**
 * 
 * @created Oct 5, 2011
 * @author double-u
 */
public class BadAPICallException extends Exception {

    protected String methodName;
    protected List args;
    protected String errorName, errorDescription;
    protected List<String> info;
    protected Object origin;

    public BadAPICallException(String methodName, List args) {
        this(methodName, args, "Call " + methodName + " failed", "");
    }

    public BadAPICallException(String methodName, List params, String errorName, List<String> info) {
        this.methodName = methodName;
        this.args = params;
        this.errorName = errorName;
        this.info = info;
        this.errorDescription = getErrorDescription();
        String betterDescription = I18N.getText(errorName);
        if (betterDescription != null) {
            this.errorDescription = betterDescription;
        }
    }

    public BadAPICallException(String methodName, List params, String errorName, String errorDescription) {
        this.methodName = methodName;
        this.args = params;
        this.errorName = errorName;
        String betterDescription = I18N.getText(errorName);
        this.errorDescription = (betterDescription == null ? errorDescription : betterDescription);
    }

    public List getArgs() {
        return args;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getErrorName() {
        return errorName;
    }

    /**
     * When you can provide something more helpful in describing the error
     * @param errorName 
     */
    public void redefineErrorName(String errorName) {
        this.errorName = errorName;
        
        if (I18N.hasText(errorName)) {
            this.errorName = I18N.getText(errorName);
        }
        if (I18N.hasText(errorName + "_MESSAGE")) {
            this.errorDescription = I18N.getText(errorName + "_MESSAGE");
        }
    }

    public final String getErrorDescription() {
        if (this.errorDescription != null && !this.errorDescription.isEmpty()) {
            return errorDescription;
        } else if (info != null) {
            String decription = null;
            if (info.size() > 2) {
                decription = info.get(2);
            } else if (info.size() == 2) {
                decription = info.get(1);
            } else {
                decription = errorName;
            }

            return decription;
        }
        return "No description available";
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public List<String> getInfo() {
        return info;
    }

    public void setInfo(List<String> info) {
        this.info = info;
    }

    @Override
	public String toString() {
        return "The method " + methodName + " returned following error " + errorName + " : " + getErrorDescription();
    }

    public Object getOrigin() {
        return origin;
    }

    public void setOrigin(Object origin) {
        this.origin = origin;
    }

    @Override
    public String getMessage() {
        if (errorDescription == null) {
            errorDescription = getErrorDescription();
        }
        return errorDescription;
    }
}
