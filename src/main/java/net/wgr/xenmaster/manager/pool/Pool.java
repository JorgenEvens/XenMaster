/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.manager.pool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import net.wgr.utility.GlobalExecutorService;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 23, 2011
 * @author double-u
 */
public class Pool implements Runnable {

    protected LinkedList<Worker> workers;
    protected DatagramSocket socket;
    protected Thread thread;
    protected boolean run;

    public boolean becomeMaster() {
        // [Harbinger] Assuming control
        return true;
    }

    public void boot() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        socket.connect(InetAddress.getByName("255.255.255.255"), 54321);
        socket.setBroadcast(true);
        
        run = true;
        thread = new Thread(this);
        
        GlobalExecutorService.get().scheduleAtFixedRate(new AliveBroadcaster(), 0, 5, TimeUnit.SECONDS);
    }
    
    protected class AliveBroadcaster implements Runnable {

        @Override
        public void run() {
            XMPacket xmp = new XMPacket();
            xmp.alive = true;
            byte[] data = xmp.getContents();
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.send(packet);
            } catch (IOException ex) {
                Logger.getLogger(getClass()).error("Failed to broadcast alive packet", ex);
            }
        }
        
    }

    @Override
    public void run() {
        DatagramPacket packet = null;
        while (run) {
            try {
                socket.receive(packet);
                process(packet);
            } catch (IOException ex) {
                Logger.getLogger(getClass()).warn("Receiving UDP packet failed", ex);
            }
        }
    }

    /**
     * Prefix     Alive - \
     * +----------------+ | - - - - - - - - - - - - - - - +
     * +1011010111010000+ 1 
     * @param packet 
     */
    protected void process(DatagramPacket packet) {
        if (packet.getLength() < 2) {
            return;
        }

        // 101101011101 ~ 29/09  = birthday of initial commit
        if (!(packet.getData()[0] == 0b10110101 && packet.getData()[1] == 0b1101_0000)) {
            return;
        }
        
        
    }
    
    public class XMPacket {
        // Alive packet, does not require further processing
        public boolean alive;
        public String contents;
        
        public byte[] getContents() {
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.put(Integer.valueOf(0b10110101).byteValue());
            bb.put(Integer.valueOf(0b1101_0000).byteValue());
            
            short flags = 0;
            flags += (alive ? 0b1 : 0b0);
            
            bb.putShort(flags);
            return bb.array();
        }
        
    }
}
