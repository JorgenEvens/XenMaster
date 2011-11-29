/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api.helpers;

import java.net.InetAddress;
import java.util.HashMap;
import net.wgr.xenmaster.api.Host;
import net.wgr.xenmaster.api.PBD;
import net.wgr.xenmaster.api.SR;
import net.wgr.xenmaster.controller.BadAPICallException;

/**
 * 
 * @created Oct 22, 2011
 * @author double-u
 */
public class NFS {

    public static String mountISORepository(String name, InetAddress target, String path, Host h) throws BadAPICallException {
        SR iso = new SR(null);
        iso.setName(name);
        iso.setDescription("ISO repository : " + name);
        HashMap<String, String> cfg = new HashMap<>();
        cfg.put("location", target.getCanonicalHostName() + ":" + path);
        iso.setSmConfig(cfg);
        // Provide type for backend
        HashMap<String, String> otherCfg = new HashMap<>();
        otherCfg.put("storageType", "nfs");
        iso.setOtherConfig(otherCfg);
        String ref = iso.introduce(SR.Type.ISO, "iso", true);

        PBD pbd = new PBD(null);
        pbd.create(iso, h, cfg);
        pbd.plug();
        
        return ref;
    }
}
