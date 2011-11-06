/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.connectivity.tftp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import net.wgr.utility.GlobalExecutorService;
import org.apache.commons.net.tftp.ExtendedTFTP;
import org.apache.commons.net.tftp.TFTPAckPacket;
import org.apache.commons.net.tftp.TFTPPXEDataPacket;
import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.apache.commons.net.tftp.TFTPOptionAckPacket;
import org.apache.commons.net.tftp.TFTPOptionReadRequestPacket;
import org.apache.commons.net.tftp.TFTPPacket;
import org.apache.commons.net.tftp.TFTPPacketException;
import org.apache.commons.net.tftp.TFTPReadRequestPacket;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 27, 2011
 * @author double-u
 */
public class TFTPServer implements Runnable {

    protected Thread thread;
    protected boolean run;
    protected ExtendedTFTP tftp;
    protected InetAddress clientAddress;
    protected InputStream dataInputStream;
    protected int blockNumber, blockSize = 512;
    protected LinkedList<ActivityListener> listeners;
    protected int count;
    protected ResendTask resendTask;

    public TFTPServer() {
        tftp = new ExtendedTFTP();
        tftp.beginBufferedOps();
        tftp.setDefaultTimeout(0);

        listeners = new LinkedList<>();
        resendTask = new ResendTask();
        GlobalExecutorService.get().scheduleAtFixedRate(resendTask, 500, 500, TimeUnit.MILLISECONDS);
    }

    public void addListener(ActivityListener al) {
        listeners.push(al);
    }

    public void boot() {
        run = true;

        thread = new Thread(this, "TFTP server");
        thread.start();
    }

    @Override
    public void run() {
        while (run) {
            if (!tftp.isOpen()) {
                try {
                    tftp.open(69, InetAddress.getByName("0.0.0.0"));
                    tftp.beginBufferedOps();
                } catch (SocketException | UnknownHostException ex) {
                    Logger.getLogger(getClass()).error("TFTP listening failed", ex);
                    run = false;
                    return;
                }
            }

            try {
                TFTPPacket packet = tftp.bufferedReceive();
                count++;
                handlePacket(packet);
            } catch (TFTPPacketException | IOException ex) {
                Logger.getLogger(getClass()).error("TFTP receive failed", ex);
            }
        }

        tftp.endBufferedOps();
        tftp.close();
    }

    protected void handlePacket(final TFTPPacket packet) throws IOException {
        switch (packet.getType()) {
            case TFTPReadRequestPacket.READ_REQUEST:
                if (clientAddress == null) {
                    TFTPOptionReadRequestPacket request = (TFTPOptionReadRequestPacket) packet;
                    dataInputStream = null;

                    try {
                        Logger.getLogger(getClass()).debug("Request for : " + request.getFilename() + " received from " + packet.getAddress().getCanonicalHostName()
                                + ":" + packet.getPort());

                        for (ActivityListener al : listeners) {
                            InputStream is = al.pathRequest(request);
                            if (is != null) {
                                dataInputStream = is;
                            }
                            break;
                        }

                        if (dataInputStream == null) {
                            Logger.getLogger(getClass()).debug("No ActivityListener provided valid InputStream for TFTP request");
                            tftp.bufferedSend(new TFTPErrorPacket(packet.getAddress(), packet.getPort(), TFTPErrorPacket.FILE_NOT_FOUND, request.getFilename()));
                            return;
                        }

                        if (request.getOptions().size() > 0) {
                            HashMap<String, Integer> acks = new HashMap<>();
                            for (Map.Entry<String, Integer> entry : request.getOptions().entrySet()) {
                                switch (entry.getKey()) {
                                    case "blksize":
                                        blockSize = entry.getValue();
                                        acks.put(entry.getKey(), blockSize);
                                        tftp.restartBufferedOps(blockSize + 4);
                                        break;
                                    case "tsize":
                                        // Client wants to know transfer size
                                        acks.put(entry.getKey(), dataInputStream.available());
                                        break;
                                }
                            }

                            blockNumber = 0;
                            clientAddress = packet.getAddress();

                            tftp.bufferedSend(new TFTPOptionAckPacket(packet.getAddress(), packet.getPort(), acks));

                            return;
                        }

                        blockNumber = 1;
                        byte[] data = new byte[blockSize];
                        final int bytesRead = dataInputStream.read(data);
                        tftp.bufferedSend(new TFTPPXEDataPacket(packet.getAddress(), packet.getPort(), blockNumber, data, 0, bytesRead));
                        // if more blocks to send
                        if (bytesRead == blockSize) {
                            clientAddress = packet.getAddress();
                        } else {
                            dataInputStream.close();
                            clientAddress = null;
                        }
                    } catch (FileNotFoundException ex) {
                        tftp.bufferedSend(new TFTPErrorPacket(packet.getAddress(), packet.getPort(), TFTPErrorPacket.FILE_NOT_FOUND, ex.getMessage()));
                        clientAddress = null;
                    }
                }
                break;
            case TFTPReadRequestPacket.ACKNOWLEDGEMENT:
                if (packet.getAddress().equals(clientAddress)) {
                    TFTPAckPacket ackPacket = (TFTPAckPacket) packet;
                    Logger.getLogger(getClass()).debug("ACK : " + ackPacket.getBlockNumber() + " ~ " + blockNumber * blockSize + "/" + dataInputStream.available() + " " + ackPacket.getAddress().getCanonicalHostName());
                    // Check if client ACKd correctly
                    if (ackPacket.getBlockNumber() == blockNumber) {
                        // send next block
                        final byte[] data = new byte[blockSize];
                        final int bytesRead = dataInputStream.read(data);
                        blockNumber++;
                        TFTPPXEDataPacket dataPacket = new TFTPPXEDataPacket(packet.getAddress(), packet.getPort(), blockNumber, data, 0, bytesRead);
                        
                        Logger.getLogger(getClass()).debug("Sending " + blockNumber + " to " + packet.getAddress().getCanonicalHostName() + " with " + bytesRead + " bytes");
                        
                        tftp.bufferedSend(dataPacket);
                        resendTask.set(dataPacket, ackPacket.getBlockNumber());
                        
                        // It is done
                        if (bytesRead < blockSize) {
                            clientAddress = null;
                            dataInputStream.close();
                            resendTask.chill();
                        }
                    }
                }
                break;
            case TFTPReadRequestPacket.ERROR:
                TFTPErrorPacket tep = (TFTPErrorPacket) packet;
                Logger.getLogger(getClass()).warn("TFTP error : " + tep.getMessage());
                clientAddress = null;
                blockNumber = 0;
                break;
        }
    }
    
    protected class ResendTask extends TimerTask {
        
        protected TFTPPXEDataPacket packet;
        protected int blockCount;
        
        public void set(TFTPPXEDataPacket packet, int blockCount) {
            this.packet = packet;
            this.blockCount = blockCount;
        }
        
        public void chill() {
            this.packet = null;
        }

        @Override
        public void run() {
            if (packet == null) return;
            if (blockNumber > blockCount) {
                try {
                    Logger.getLogger(getClass()).debug("Resending " + blockNumber);
                    tftp.bufferedSend(packet);
                } catch (IOException ex) {
                    Logger.getLogger(getClass()).warn("TFTP resend failed", ex);
                }
            }
        }
        
    }

    public static interface ActivityListener {

        public InputStream pathRequest(TFTPOptionReadRequestPacket packet);
    }
}
