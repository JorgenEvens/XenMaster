/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package org.apache.commons.net.tftp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Map;

/***
 * A final class derived from TFTPPacket defining the TFTP Acknowledgment
 * packet type.
 * <p>
 * Details regarding the TFTP protocol and the format of TFTP packets can
 * be found in RFC 783.  But the point of these classes is to keep you
 * from having to worry about the internals.  Additionally, only very
 * few people should have to care about any of the TFTPPacket classes
 * or derived classes.  Almost all users should only be concerned with the
 * {@link org.apache.commons.net.tftp.TFTPClient} class
 * {@link org.apache.commons.net.tftp.TFTPClient#receiveFile receiveFile()}
 * and
 * {@link org.apache.commons.net.tftp.TFTPClient#sendFile sendFile()}
 * methods.
 * <p>
 * <p>
 * @author Daniel F. Savarese
 * @see TFTPPacket
 * @see TFTPPacketException
 * @see TFTP
 ***/
public final class TFTPOptionAckPacket extends TFTPPacket {

    /*** The block number being acknowledged by the packet. ***/
    protected Map<String, Integer> options;

    /***
     * Creates an acknowledgment packet to be sent to a host at a given port
     * acknowledging receipt of a block.
     * <p>
     * @param destination  The host to which the packet is going to be sent.
     * @param port  The port to which the packet is going to be sent.
     * @param blockNumber  The block number being acknowledged.
     ***/
    public TFTPOptionAckPacket(InetAddress destination, int port, Map<String, Integer> options) {
        super(ExtendedTFTPPacket.OPTION_ACKNOWLEDGEMENT, destination, port);
        this.options = options;
    }

    /***
     * Creates an acknowledgment packet based from a received
     * datagram.  Assumes the datagram is at least length 4, else an
     * ArrayIndexOutOfBoundsException may be thrown.
     * <p>
     * @param datagram  The datagram containing the received acknowledgment.
     * @throws TFTPPacketException  If the datagram isn't a valid TFTP
     *         acknowledgment packet.
     ***/
    TFTPOptionAckPacket(DatagramPacket datagram) throws TFTPPacketException {
        super(ExtendedTFTPPacket.OPTION_ACKNOWLEDGEMENT, datagram.getAddress(),
                datagram.getPort());
        byte[] data;

        data = datagram.getData();

        if (getType() != data[1]) {
            throw new TFTPPacketException("TFTP operator code does not match type.");
        }

        //.blockNumber = (((data[2] & 0xff) << 8) | (data[3] & 0xff));
    }

    /***
     * This is a method only available within the package for
     * implementing efficient datagram transport by eliminating buffering.
     * It takes a datagram as an argument, and a byte buffer in which
     * to store the raw datagram data.  Inside the method, the data
     * is set as the datagram's data and the datagram returned.
     * <p>
     * @param datagram  The datagram to create.
     * @param data The buffer to store the packet and to use in the datagram.
     * @return The datagram argument.
     ***/
    @Override
    DatagramPacket _newDatagram(DatagramPacket datagram, byte[] data) {
        data[0] = 0;
        data[1] = (byte) ExtendedTFTPPacket.OPTION_ACKNOWLEDGEMENT;

        int index = 2;
        for (Map.Entry<String, Integer> entry : options.entrySet()) {
            char[] chars = entry.getKey().toCharArray();
            for (int i = 0; i < chars.length; i++) {
                data[index++] = (byte) chars[i];
            }

            data[index++] = 0;
            chars = ("" + entry.getValue()).toCharArray();
            for (int i = 0; i < chars.length; i++) {
                data[index++] = (byte) chars[i];
            }
            data[index++] = 0;
        }

        datagram.setAddress(_address);
        datagram.setPort(_port);
        datagram.setData(data);
        datagram.setLength(index);

        return datagram;
    }

    /***
     * Creates a UDP datagram containing all the TFTP
     * acknowledgment packet data in the proper format.
     * This is a method exposed to the programmer in case he
     * wants to implement his own TFTP client instead of using
     * the {@link org.apache.commons.net.tftp.TFTPClient}
     * class.  Under normal circumstances, you should not have a need to call this
     * method.
     * <p>
     * @return A UDP datagram containing the TFTP acknowledgment packet.
     ***/
    @Override
    public DatagramPacket newDatagram() {
        byte[] data = new byte[SEGMENT_SIZE];

        data[0] = 0;
        data[1] = (byte) ExtendedTFTPPacket.OPTION_ACKNOWLEDGEMENT;

        int index = 2;
        for (Map.Entry<String, Integer> entry : options.entrySet()) {
            char[] chars = entry.getKey().toCharArray();
            for (int i = 0; i < chars.length; i++) {
                data[index++] = (byte) chars[i];
            }

            data[index++] = 0;
            chars = ("" + entry.getValue()).toCharArray();
            for (int i = 0; i < chars.length; i++) {
                data[index++] = (byte) chars[i];
            }
            data[index++] = 0;
        }

        return new DatagramPacket(data, data.length, _address, _port);
    }

    public Map<String, Integer> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Integer> options) {
        this.options = options;
    }
}
