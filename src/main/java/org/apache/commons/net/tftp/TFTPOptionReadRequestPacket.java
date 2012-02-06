/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package org.apache.commons.net.tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * RFC 2347 / 2348 / 2349
 * 
 * Providing a more complete implementation for TFTPReadRequestPacket
 * 
 * I make a huge effort not to shout at people abusing underscores. 
 * I will however shout at people that do not make their libraries properly extendable.
 * If anything, this is way below the quality level an Apache Commons lib should have
 * 
 * @created Oct 28, 2011
 * @author double-u
 */
public final class TFTPOptionReadRequestPacket extends TFTPRequestPacket {

    protected HashMap<String, Integer> options;

    /***
     * Creates a read request packet to be sent to a host at a
     * given port with a filename and transfer mode request.
     * <p>
     * @param destination  The host to which the packet is going to be sent.
     * @param port  The port to which the packet is going to be sent.
     * @param filename The requested filename.
     * @param mode The requested transfer mode.  This should be on of the TFTP
     *        class MODE constants (e.g., TFTP.NETASCII_MODE).
     ***/
    public TFTPOptionReadRequestPacket(InetAddress destination, int port, String filename, int mode) {
        super(destination, port, TFTPPacket.READ_REQUEST, filename, mode);
        options = new HashMap<>();
    }

    /***
     * Creates a read request packet of based on a received
     * datagram and assumes the datagram has already been identified as a
     * read request.  Assumes the datagram is at least length 4, else an
     * ArrayIndexOutOfBoundsException may be thrown.
     * <p>
     * @param datagram  The datagram containing the received request.
     * @throws TFTPPacketException  If the datagram isn't a valid TFTP
     *         request packet.
     ***/
    TFTPOptionReadRequestPacket(DatagramPacket datagram) throws TFTPPacketException {
        super(TFTPPacket.READ_REQUEST, datagram);
        options = new HashMap<>();

        byte[] data = datagram.getData();
        int index = 0, zeroCount = 0;
        boolean goAhead = false;

        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0) {
                zeroCount++;
            }
            if (zeroCount == 2) {
                index = i + 2;
            }
            if (zeroCount == 3) {
                // We have ourselves a custom packet
                goAhead = true;
                break;
            }
        }

        if (!goAhead) {
            return;
        }

        while (index < data.length) {
            if (data[index + 1] == 0 && data[index + 2] == 0) {
                break;
            }

            // Check for option
            StringBuilder name = new StringBuilder();
            for (int i = index; i < data.length; i++) {
                if (data[i] == 0) {
                    index = i + 1;
                    break;
                }
                name.append((char) data[i]);
            }

            if (options.containsKey(name.toString())) {
                break;
            }

            StringBuilder value = new StringBuilder();
            for (int i = index; i < data.length; i++) {
                // Check if next byte isn't zero as well -- if it is, the current zero forms the value
                if (data[i] == 0) {
                    index = i + 1;
                    if (value.length() == 0) {
                        value.append("0");
                        index++;
                    }
                    break;
                }
                value.append((char) data[i]);
            }

            try {
                options.put(name.toString(), Integer.parseInt(value.toString()));
            } catch (NumberFormatException ex) {
                // failz
            }
        }
    }

    public Map<String, Integer> getOptions() {
        return options;
    }
}
