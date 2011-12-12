/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api.plugins;

import com.google.gson.Gson;
import java.util.HashMap;
import net.wgr.xenmaster.api.Host;
import net.wgr.xenmaster.controller.BadAPICallException;

/**
 * 
 * @created Dec 12, 2011
 * @author double-u
 */
public class FileSystem {
    protected final static String FILESYTEM_PLUGIN = "xm-filesystem";
    protected static Gson gson = new Gson();
    
    public static String[] getDiskDevices(Host host) throws BadAPICallException {
        String result = host.callPlugin(FILESYTEM_PLUGIN, "disks", new HashMap<String, String>());
        String[] str = gson.fromJson(result, String[].class);
        return str;
    }
}
