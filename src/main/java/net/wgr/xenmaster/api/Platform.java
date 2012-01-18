/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.api;

import java.util.Map;

/**
 * 
 * @created Jan 18, 2012
 * @author double-u
 */
public class Platform extends XenApiMapField {
    protected boolean apic, acpi, viridian, pae;

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
    
}
