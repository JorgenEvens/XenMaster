/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.pool;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import net.wgr.rmi.Remote;
import org.apache.log4j.Logger;

/**
 * 
 * @created Nov 2, 2011
 * @author double-u
 */
public class WorkerFactory {

    public static Worker getWorkerWithAddress(InetAddress addr) {
        try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress(addr, Pool.PORT), 500);
            Remote r = new Remote();
            r.boot(sock);
            return r.getCaller().getProxyForType(Worker.class);
        } catch (IOException ex) {
            Logger.getLogger(WorkerFactory.class).error("Connection to worker failed", ex);
        }
        return null;
    }
    
    public static Worker getLocalWorker() {
        Worker w = null;
        if (!Pool.isMasterActive()) {
            // [Harbinger] Assuming control
        } else {
            DefaultWorker worker = new DefaultWorker();
            
        }
        return w;
    }

    
}
