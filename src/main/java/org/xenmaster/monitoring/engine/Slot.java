/*
 * Slot.java
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
package org.xenmaster.monitoring.engine;

import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

import net.wgr.settings.Settings;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.xenmaster.api.Host;

/**
 * 
 * @created Jan 31, 2012
 * @author double-u
 */
public class Slot implements Comparable<Slot> {

    protected String reference;
    public long lastPolled;
    private URLConnection connection;

    public Slot(Host host) {
        this.reference = host.getReference();
    }

    public String getReference() {
        return reference;
    }

    @Override
    public int compareTo(Slot o) {
        return (int) (o.lastPolled - lastPolled);
    }

    @Override
	public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Slot other = (Slot) obj;
        if (!Objects.equals(this.reference, other.reference)) {
            return false;
        }
        return true;
    }

    @Override
	public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.reference);
        hash = 41 * hash + (int) (this.lastPolled ^ (this.lastPolled >>> 32));
        return hash;
    }

    public URLConnection getConnection() {
        if (connection == null) {
            try {
                URL url = new URL("" + "/host_rrd");
                URLConnection uc = url.openConnection();
                byte[] auth = (Settings.getInstance().getString("Xen.User") + ':' + Settings.getInstance().getString("Xen.Password")).getBytes("UTF-8");
                uc.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64(auth)));
                uc.connect();
            } catch (Exception ex) {
                Logger.getLogger(getClass()).error("Failed to retrieve statistics", ex);
            }
        }

        return connection;
    }
}
