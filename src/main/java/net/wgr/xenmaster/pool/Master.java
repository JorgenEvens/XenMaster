/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.pool;

import java.util.List;

/**
 * 
 * @created Nov 1, 2011
 * @author double-u
 */
public interface Master extends Worker {
    public List<Worker> getWorkers();
    public <T extends Worker> List<T> getWorkers(Class<T> type);
    
    /**
     * When a friend of a worker sees that worker disappear, he will replace him and notify the manager
     * The manager can redistribute loads if wanted
     * @param friend
     * @param worker 
     */
    public void workerReplacedByFriend(Friend friend, Worker worker) throws WorkerIsAliveException;
}
