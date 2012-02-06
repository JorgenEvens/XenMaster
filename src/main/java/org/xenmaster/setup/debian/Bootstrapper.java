/*
 * Bootstrapper.java
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
package org.xenmaster.setup.debian;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.wgr.settings.Settings;
import net.wgr.utility.Network;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.tftp.TFTPOptionReadRequestPacket;
import org.apache.log4j.Logger;
import org.xenmaster.connectivity.tftp.TFTPServer;

/**
 * 
 * @created Oct 27, 2011
 * @author double-u
 */
public class Bootstrapper {

    protected TFTPServer tftpd;
    protected HashMap<String, String> preseedValues;

    public Bootstrapper() {
        tftpd = new TFTPServer();
        preseedValues = new HashMap<>();
    }

    public void boot() {
        tftpd.addListener(new PXEListener());
        tftpd.boot();

        preseedValues.put("menuItemLabel", "^Install Debian");
    }
    
    public void waitForServerToQuit() throws InterruptedException {
        tftpd.waitTillQuit();
    }

    protected class PXEListener implements TFTPServer.ActivityListener {

        @Override
        public InputStream pathRequest(TFTPOptionReadRequestPacket packet) {
            try {
                File path = new File(Settings.getInstance().getString("StorePath") + "/netboot/" + packet.getFilename());
                if (!path.exists()) {
                    return null;
                }
                File f = path.getAbsoluteFile();
                if (f.exists()) {
                    FileInputStream fis = new FileInputStream(f);
                    if (f.getName().equals("txt.cfg")) {
                        preseedValues.put("preseedUrl", "http://" + Network.getHostAddressInSubnet(packet.getAddress().getHostAddress(), "255.255.0.0").getHostAddress() + ":" + Settings.getInstance().get("WebApplicationPort") + "/setup/xapi");

                        String txt = IOUtils.toString(fis);
                        for (Map.Entry<String, String> entry : preseedValues.entrySet()) {
                            // todo regex
                            txt = txt.replace("#{" + entry.getKey() + "}", entry.getValue());
                        }
                        
                        return new ByteArrayInputStream(txt.getBytes("UTF-8"));
                    }
                    return fis;
                }
            } catch (IOException ex) {
                Logger.getLogger(getClass()).error("File not found", ex);
            }
            return null;
        }
    }
}
