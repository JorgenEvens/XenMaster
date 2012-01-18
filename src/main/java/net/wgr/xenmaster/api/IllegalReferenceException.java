/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

/**
 * 
 * @created Jan 18, 2012
 * @author double-u
 */
public class IllegalReferenceException extends Exception {

    @Override
    public String getMessage() {
        return "The reference provided is null or the referenced object does not exist";
    }
    
}
