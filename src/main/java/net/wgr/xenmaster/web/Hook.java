/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.web;

import net.wgr.server.web.handling.WebCommandHandler;
import net.wgr.wcp.Command;

/**
 * 
 * @created Oct 1, 2011
 * @author double-u
 */
public class Hook extends WebCommandHandler {

    public Hook() {
        super("xen");
    }

    public Object execute(Command cmd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
