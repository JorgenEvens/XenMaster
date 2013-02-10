/*
 * NFS.java
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
package org.xenmaster.api.helpers;

import java.net.InetAddress;
import java.util.HashMap;

import org.xenmaster.api.entities.Host;
import org.xenmaster.api.entities.PBD;
import org.xenmaster.api.entities.SR;
import org.xenmaster.api.store.Store;
import org.xenmaster.controller.BadAPICallException;

/**
 * 
 * @created Oct 22, 2011
 * @author double-u
 */
public class NFS {

    public static String mountISORepository(String name, InetAddress target, String path, Host h) throws BadAPICallException {
        SR iso = new SR();
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
        String pbdRef = pbd.create(iso, h, cfg);
        pbd.plug();
        
        Store.get().put(SR.ASSOCIATED_PBDS, ref, pbdRef);
        
        return ref;
    }
    
    public static String createNFSSR(String name, String description, InetAddress target, String path, Host h) throws BadAPICallException {
        SR sr = new SR();
        sr.setName(name);
        sr.setDescription(description);
        HashMap<String, String> cfg = new HashMap<>();
        cfg.put("server", target.getCanonicalHostName());
        cfg.put("serverpath", path);
        cfg.put("options", "");
        sr.setSmConfig(new HashMap<String, String>());
        String ref = sr.create(h, cfg, SR.Type.NFS, "user", true, 0);
        return ref;
    }
}
