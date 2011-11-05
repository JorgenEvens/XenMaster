/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.pool;

import net.wgr.xenmaster.entities.Host;

/**
 * 
 * @created Nov 1, 2011
 * @author double-u
 */
public interface Friend extends Worker {
    public void backupSession(String sessionReference, Host host) throws InvalidBackupInformationException;
}
