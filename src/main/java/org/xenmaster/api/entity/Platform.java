/*
 * Platform.java
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
package org.xenmaster.api.entities;

import org.xenmaster.api.util.XenApiMapField;
import java.util.Map;

/**
 *
 * @created Jan 18, 2012
 * @author double-u
 */
public class Platform extends XenApiMapField {

    protected boolean apic, acpi, viridian, pae;
    protected boolean pvfb;

    public Platform(Map<String, String> values) {
        super(values);
    }

    public Platform() {
    }

    public boolean hasAcpi() {
        return acpi;
    }

    public void setAcpi(boolean acpi) {
        this.acpi = acpi;
    }

    public boolean hasApic() {
        return apic;
    }

    public void setApic(boolean apic) {
        this.apic = apic;
    }

    public boolean hasPae() {
        return pae;
    }

    public void setPae(boolean pae) {
        this.pae = pae;
    }

    public boolean isViridian() {
        return viridian;
    }

    public void setViridian(boolean viridian) {
        this.viridian = viridian;
    }

    public boolean isPvfb() {
        return pvfb;
    }

    public void setPvfb(boolean pvfb) {
        this.pvfb = pvfb;
    }
    
    
}
