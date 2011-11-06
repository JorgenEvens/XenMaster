/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.wgr.server.web.handling.WebHook;
import net.wgr.settings.Settings;
import net.wgr.utility.Network;
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
        switch (rb.getPathParts()[0]) {
            case "xapi":
                fis = new FileInputStream("store/setup/preseed-template.txt");
                break;
            case "post-install.sh":
                fis = new FileInputStream("store/setup/post-install.sh");
                break;
            case "motd":
                fis = new FileInputStream("store/setup/motd");
                Logger.getLogger(getClass()).info(rb.getRequest().getParameter("IP") + " completed network install");
                break;
            default:
                rb.getBaseRequest().setHandled(false);
                return;
        }

        if (fis != null) {
            rb.replyWithString(parseTemplate(fis, rb.getRequest().getRemoteAddr()));
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
