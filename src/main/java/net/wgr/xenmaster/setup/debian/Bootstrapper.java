/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.setup.debian;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.wgr.settings.Settings;
import net.wgr.xenmaster.connectivity.tftp.TFTPServer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.tftp.TFTPOptionReadRequestPacket;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.log4j.Logger;

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

    protected class PXEListener implements TFTPServer.ActivityListener {

        @Override
        public InputStream pathRequest(TFTPOptionReadRequestPacket packet) {
            try {
                File path = new File("/Users/double-u/Downloads/netboot/" + packet.getFilename());
                if (!path.exists()) {
                    return null;
                }
                File f = path.getAbsoluteFile();
                if (f.exists()) {
                    try {
                        SubnetUtils utils = new SubnetUtils(packet.getAddress().getHostAddress(), "255.255.255.0");
                        // todo determine correct interface address
                        for (NetworkInterface nic : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                            for (InetAddress addr : Collections.list(nic.getInetAddresses())) {
                                if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                                    if (utils.getInfo().isInRange(addr.getHostAddress())) {
                                        preseedValues.put("preseedUrl", "http://" + addr.getCanonicalHostName() + ":" + Settings.getInstance().get("WebApplicationPort") + "/setup/xapi");
                                    }
                                }
                            }
                        }

                    } catch (SocketException ex) {
                        Logger.getLogger(getClass()).error("Failed to configure preseeding", ex);
                    }

                    FileInputStream fis = new FileInputStream(f);
                    if (f.getName().equals("txt.cfg")) {
                        String txt = IOUtils.toString(fis);
                        for (Map.Entry<String, String> entry : preseedValues.entrySet()) {
                            // todo regex
                            txt = txt.replace("#{" + entry.getKey() + "}", entry.getValue());
                        }
                        ByteArrayInputStream bais = new ByteArrayInputStream(txt.getBytes("UTF-8"));
                        return bais;
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
