/*
 * XMPacket.java
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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * Prefix     Alive - \ / - Master
 * +----------------+ | | - - - - - - - - - - - - - - +
 * +1011010111010000+ 1 1
 * @param packet 
 */

/**
 * 
 * @created Nov 6, 2011
 * @author double-u
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

        byte flags = 0;
        flags += (alive ? 0b1 : 0b0);
        flags += (master ? 0b10 : 0b0);

        bb.put(flags);
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
        if (!(data[0] == (byte) 0b10110101 && data[1] == (byte) 0b1101_0000)) {
            Logger.getLogger(getClass()).debug("Illegal prefix for packet");
            return;
        }

        byte flags = data[2];
        alive = (flags & 0b1) != 0;
        master = (flags & 0b10) != 0;

        contents = new String(ArrayUtils.subarray(data, 3, length));
    }
}
