/*
 * Pool.java
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
package org.xenmaster.pool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import net.wgr.rmi.Remote;

import org.apache.log4j.Logger;
import org.jgroups.JChannel;
import org.jgroups.protocols.BPING;
import org.jgroups.protocols.MERGE2;
import org.jgroups.protocols.UDP;
import org.jgroups.stack.ProtocolStack;

/**
 * 
 * @created Oct 23, 2011
 * @author double-u
 */
public class Pool {

    protected HashMap<InetAddress, Worker> workers;
    protected HashMap<InetAddress, Remote> remotes;
    // Pool Orchestrating Protocol
    protected DatagramSocket popSocket;
    protected ServerSocket rmiSocket;
    protected Thread rmi;
    protected boolean run, master;
    protected Worker local;
    protected JChannel channel;
    // The port number is completely random ... or is it?
    public final static int PORT = 24515;
    private volatile static Pool instance;

    private Pool() {
        workers = new HashMap<>();
        rmi = new Thread(new RMIListener(), "Pool RMI");
        //channel = buildChannel();
    }

    public static Pool get() {
        if (instance == null) {
            instance = new Pool();
        }
        return instance;
    }

    protected JChannel buildChannel() {
        JChannel jc = new JChannel(false);
        ProtocolStack stack = new ProtocolStack();
        jc.setProtocolStack(stack);

        // Transport
        UDP udp = new UDP();
        udp.setBindPort(PORT);
        stack.addProtocol(udp);

        // Discovery
        BPING ping = new BPING();
        stack.addProtocol(ping);

        // Group merge
        MERGE2 merge = new MERGE2();
        stack.addProtocol(merge);

        try {
            stack.init();
        } catch (Exception ex) {
            Logger.getLogger(getClass()).error("Failed to connect to cluster", ex);
        }
        return jc;
    }

    public JChannel getChannel() {
        return channel;
    }

    public Map<InetAddress, Worker> getWorkers() {
        return workers;
    }

    public void boot() throws SocketException, UnknownHostException, IOException {
        //popSocket = new DatagramSocket(PORT);

        //rmiSocket = new ServerSocket(PORT);

        run = true;
        //wpop.start();
        //rmi.start();
        //GlobalExecutorService.get().scheduleAtFixedRate(new AliveBroadcaster(), 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        // Send "going down" msg to pool master or to friend if this is the master
        run = false;
    }

    protected class AliveBroadcaster implements Runnable {

        protected DatagramSocket sock;

        protected AliveBroadcaster() throws SocketException, UnknownHostException {
            sock = new DatagramSocket();
            sock.connect(InetAddress.getByName("255.255.255.255"), PORT);
            sock.setBroadcast(true);
        }

        @Override
        public void run() {
            try {
                XMPacket xmp = new XMPacket();
                xmp.alive = true;
                xmp.master = master;
                byte[] data = xmp.getContents();
                DatagramPacket packet = new DatagramPacket(data, data.length);
                try {
                    sock.send(packet);
                } catch (IOException ex) {
                    Logger.getLogger(getClass()).error("Failed to broadcast alive packet", ex);
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass()).error("Exception", ex);
            }
        }
    }

    protected class RMIListener implements Runnable {

        @Override
        public void run() {
            while (run) {
                try {
                    Socket sock = rmiSocket.accept();
                    Remote r = new Remote();
                    r.boot(sock);
                    r.getReceiver().addInvocationTarget(local);
                } catch (IOException ex) {
                    Logger.getLogger(getClass()).warn("Accepting RMI connection failed", ex);
                }
            }
        }
    }

    public static boolean isMasterActive() {
        boolean masterDetected = false;

        for (Worker w : Pool.get().getWorkers().values()) {
            if (w instanceof Master) {
                masterDetected = true;
                break;
            }
        }

        if (masterDetected) {
            return true;
        }

        Logger.getLogger(Pool.class).info("Waiting for 2 seconds for any master to come online");
        try {
            Thread.sleep(2000);
            for (Worker w : Pool.get().getWorkers().values()) {
                if (w instanceof Master) {
                    masterDetected = true;
                    break;
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Pool.class).error("Waiting for master failed", ex);
        }

        if (masterDetected) {
            return true;
        } else {
            return false;
        }
    }
}
