/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.monitoring;

import net.wgr.wcp.Commander;
import net.wgr.wcp.Scope;
import net.wgr.wcp.command.Command;

/**
 * Issues requested monitoring data "formally and with authority"
 * @created Oct 30, 2011
 * @author double-u
 */
public class Emitter {
    public static void emit(LogEntry le) {
        Commander.getInstance().commandeer(new Command("log", "write", le), new Scope(Scope.Target.ALL));
    }
}
