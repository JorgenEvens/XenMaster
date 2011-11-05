/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.web;

import java.io.FileInputStream;
import java.io.IOException;
import net.wgr.server.web.handling.WebHook;
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
        switch (rb.getPathParts()[0]) {
            case "xapi":
                IOUtils.write(IOUtils.toByteArray(new FileInputStream("store/setup/preseed-template.txt")), rb.getResponseBody());
                rb.getBaseRequest().setHandled(true);
                break;
            case "post-install.sh":
                IOUtils.write(IOUtils.toByteArray(new FileInputStream("store/setup/post-install.sh")), rb.getResponseBody());
                rb.getBaseRequest().setHandled(true);
                break;
            default:
                rb.getBaseRequest().setHandled(false);
                break;
        }
    }
}
