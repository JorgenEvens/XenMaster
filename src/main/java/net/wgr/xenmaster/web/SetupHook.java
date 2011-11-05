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
        if (rb.getPathParts()[0].equals("xapi")) {
            
            IOUtils.write(IOUtils.toByteArray(new FileInputStream("conf/preseed-template.txt")), rb.getResponseBody());
            rb.getBaseRequest().setHandled(true);
        } else {
        }
    }
}
