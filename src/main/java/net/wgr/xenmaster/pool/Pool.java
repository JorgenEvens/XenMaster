/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.pool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.wgr.rmi.Remote;
import net.wgr.utility.GlobalExecutorService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 23, 2011
 * @author double-u
 */
public class Pool implements Runnable {

    protected HashMap<InetAddress, Worker> workers;
    protected HashMap<InetAddress, Remote> remotes;
    // Pool Orchestrating Protocol
    protected DatagramSocket popSocket;
    protected ServerSocket rmiSocket;
    protected Thread wpop, rmi;
    protected boolean run, master;
    protected Worker local;
    // The port number is completely random ... or is it?
    public final static int PORT = 24515;
    private static Pool instance;

    private Pool() {
        workers = new HashMap<>();
        wpop = new Thread(this, "WPOP");
        rmi = new Thread(new RMIListener(), "Pool RMI");
    }

    public static Pool get() {
        if (instance == null) {
            instance = new Pool();
        }
        return instance;
    }

    public Worker getLocalWorker() {
        if (local == null) {
            local = WorkerFactory.getLocalWorker();
        }
        return local;
    }

    public Map<InetAddress, Worker> getWorkers() {
        return workers;
    }

    public void boot() throws SocketException, UnknownHostException, IOException {
        popSocket = new DatagramSocket(PORT);

        rmiSocket = new ServerSocket(PORT);

        run = true;
        wpop.start();
        rmi.start();
        GlobalExecutorService.get().scheduleAtFixedRate(new AliveBroadcaster(), 0, 1, TimeUnit.SECONDS);
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

    @Override
    public void run() {
        DatagramPacket packet = new DatagramPacket(new byte[1512], 1512);
        while (run) {
            try {
                popSocket.receive(packet);
                process(packet);
            } catch (IOException ex) {
                Logger.getLogger(getClass()).warn("Receiving UDP packet failed", ex);
            }
        }
    }

    protected void process(DatagramPacket packet) {
        XMPacket xm = new XMPacket();
        xm.setContents(packet.getData(), packet.getLength());

        if (xm.master && master) {
            // Hey! I'm the master! Get out of here you!
            Logger.getLogger(getClass()).warn("Rogue master detected at " + packet.getSocketAddress().toString());
        }

        if (!workers.containsKey(packet.getAddress())) {
            Logger.getLogger(getClass()).info("New worker detected at " + packet.getAddress().getCanonicalHostName());
            workers.put(packet.getAddress(), WorkerFactory.getWorkerWithAddress(packet.getAddress()));
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

    /**
     * Prefix     Alive - \ / - Master
     * +----------------+ | | - - - - - - - - - - - - - - +
     * +1011010111010000+ 1 1
     * @param packet 
     */
    public class XMPacket {
        // Alive packet, does not require further processing

        public boolean alive;
        public boolean master;
        public String contents;

        public byte[] getContents() {
            ByteBuffer bb = ByteBuffer.allocate(1024);
            bb.put(Integer.valueOf(0b10110101).byteValue());
            bb.put(Integer.valueOf(0b1101_0000).byteValue());

            short flags = 0;
            flags += (alive ? 0b1 : 0b0);
            flags += (master ? 0b10 : 0b0);

            bb.putShort(flags);
            if (contents != null) {
                try {
                    bb.put(contents.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(getClass()).error("UTF-8 not recognized, file a bug please", ex);
                }
            }
            return bb.array();
        }

        public void setContents(byte[] data, int length) {
            if (length < 2) {
                return;
            }

            // 101101011101 ~ 29/09  = birthday of initial commit
            if (!(data[0] == 0b10110101 && data[1] == 0b1101_0000)) {
                Logger.getLogger(getClass()).debug("Illegal prefix for packet");
                return;
            }

            short flags = data[2];
            alive = (flags & 0b1) == 1;
            master = (flags & 0b01) == 1;

            contents = new String(ArrayUtils.subarray(data, 3, length));
        }
    }
}
