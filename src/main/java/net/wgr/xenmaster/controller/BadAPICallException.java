/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.controller;

import java.util.List;

/**
 * 
 * @created Oct 5, 2011
 * @author double-u
 */
public class BadAPICallException extends Exception {

    protected String methodName;
    protected List args;
    protected String errorDescription;

    public BadAPICallException(String methodName, List args) {
        this(methodName, args, null);
    }

    public BadAPICallException(String methodName, List params, String errorDescription) {
        this.methodName = methodName;
        this.args = params;
        this.errorDescription = errorDescription;
    }

    public List getArgs() {
        return args;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String toString() {
        return "The method " + methodName + " returned following error " + errorDescription;
    }

}
