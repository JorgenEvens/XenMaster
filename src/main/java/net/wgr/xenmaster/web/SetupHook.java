/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import net.wgr.server.web.handling.WebHook;
import net.wgr.settings.Settings;
import net.wgr.utility.Network;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @created Oct 27, 2011
 * @author double-u
 */
public class SetupHook extends WebHook {

    public SetupHook() {
        super("setup");
    }

    @Override
    public void handle(RequestBundle rb) throws IOException {
        Logger.getLogger(getClass()).info("Preseed request " + rb.getRequestURI());
        FileInputStream fis = null;
        String store = Settings.getInstance().getString("StorePath");
        switch (rb.getPathParts()[0]) {
            case "xapi":
                fis = new FileInputStream(store + "/setup/preseed-template.txt");
                break;
            case "post-install.sh":
                fis = new FileInputStream(store + "/setup/post-install.sh");
                break;
            case "motd":
                fis = new FileInputStream(store + "/setup/motd");
                Logger.getLogger(getClass()).info(rb.getRequest().getParameter("IP") + " completed network install");
                break;
            case "plugins.tar.gz":
                try (TarArchiveOutputStream tos = new TarArchiveOutputStream(new GZIPOutputStream(new BufferedOutputStream(rb.getResponseBody())))) {
                    tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
                    writePluginsToTarball(tos);
                    tos.close();
                    return;
                } catch (IOException ex) {
                    Logger.getLogger(getClass()).error("Failed to compress plugins", ex);
                }
                break;
            default:
                rb.getBaseRequest().setHandled(false);
                return;
        }

        if (fis != null) {
            rb.replyWithString(parseTemplate(fis, rb.getRequest().getRemoteAddr()));
        }
    }
    
    protected void writePluginsToTarball(TarArchiveOutputStream tos) throws IOException {
        File f = new File("store/xapi/plugins");
        if (!f.exists() || !f.isDirectory()) throw new IOException("Plugin directory is not present");
        
        for (File plugin : f.listFiles()) {
            TarArchiveEntry tae = new TarArchiveEntry(plugin);
            tae.setUserId(0);
            tae.setMode(0755);
            tos.putArchiveEntry(tae);
            IOUtils.copy(new FileInputStream(plugin), tos);
            tos.closeArchiveEntry();
        }
    }

    protected String parseTemplate(FileInputStream fis, String addr) {
        HashMap<String, String> values = new HashMap<>();
        values.put("bootstrap-server-address", Network.getHostAddressInSubnet(addr, "255.255.255.0").getCanonicalHostName() + ':' + Settings.getInstance().get("WebApplicationPort"));

        try {
            String txt = IOUtils.toString(fis);
            for (Map.Entry<String, String> entry : values.entrySet()) {
                // todo regex
                txt = txt.replace("#{" + entry.getKey() + "}", entry.getValue());
            }
            return txt;
        } catch (IOException ex) {
            Logger.getLogger(getClass()).error("Loading template file failed", ex);
        }

        return null;
    }
}
