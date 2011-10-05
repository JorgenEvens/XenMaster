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

    public BadAPICallException(String methodName, List args) {
        this.methodName = methodName;
        this.args = args;
    }

    public List getArgs() {
        return args;
    }

    public String getMethodName() {
        return methodName;
    }
}
