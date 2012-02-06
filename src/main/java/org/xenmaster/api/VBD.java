/*
 * VBD.java
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
package org.xenmaster.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xenmaster.controller.BadAPICallException;
import org.xenmaster.controller.Controller;

/**
 * 
 * @created Oct 10, 2011
 * @author double-u
 */
public class VBD extends XenApiEntity {

    @ConstructorArgument
    protected String vm;
    @ConstructorArgument
    protected String vdi;
    @ConstructorArgument
    protected String deviceName;
    @ConstructorArgument
    protected int deviceIndex;
    @ConstructorArgument
    protected boolean bootable, empty;
    @ConstructorArgument
    protected boolean unpluggable;
    @ConstructorArgument
    protected Mode mode;
    @ConstructorArgument
    protected Type type;
    protected boolean attached;
    protected int statusCode;
    protected String statusDetail;
    @Fill
    protected Map<String, String> runtimeProperties;
    @ConstructorArgument
    protected String qosAlgorithm;
    @Fill
    protected Object[] supportedQosAlgorithms;
    @Fill
    @ConstructorArgument
    protected Map<String, String> qosAlgorithmParams;
    protected String metrics;
    @Fill
    @ConstructorArgument
    protected Map<String, String> otherConfig;

    public VBD() {
        this.deviceIndex = -1;
        this.mode = Mode.RW;
        this.unpluggable = true;
    }

    public VBD(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public VBD(String ref) {
        super(ref);
    }

    public void eject() throws BadAPICallException {
        dispatch("eject");
    }

    public void insert(VDI disk) throws BadAPICallException {
        dispatch("insert", disk.getReference());
    }

    public void plug() throws BadAPICallException {
        dispatch("plug");
    }

    public void unplug(boolean force) throws BadAPICallException {
        dispatch(force ? "unplug_force" : "unplug");
    }

    public boolean isAttachable() {
        try {
            dispatch("assert_attachable");
            return true;
        } catch (BadAPICallException ex) {
            return false;
        }
    }

    public String create(VM vm, VDI vdi, String deviceName) throws BadAPICallException {
        this.vm = vm.getReference();
        if (vdi == null) {
            empty = true;
        } else {
            this.vdi = vdi.getReference();
        }
        if (this.deviceIndex == -1) {
            this.deviceIndex = vm.getNextAvailableVBDIndex();
            if (this.deviceIndex == -1) {
                ArrayList<String> info = new ArrayList<>();
                info.add(vm.getName());
                throw new BadAPICallException("VBD.create", null, "NO_FREE_VBD_SLOT", info);
            }
        }

        this.reference = (String) Controller.dispatch("VBD.create", collectConstructorArgs());
        return this.reference;
    }

    public void destroy() throws BadAPICallException {
        dispatch("destroy");
    }

    public VDI getVDI() {
        vdi = value(vdi, "get_VDI");
        return new VDI(vdi);
    }

    public VM getVM() {
        vm = value(vm, "get_VM");
        return new VM(vm);
    }

    public VBDMetrics getMetrics() {
        metrics = value(metrics, "get_metrics");
        return new VBDMetrics(metrics);
    }

    public static List<VBD> getAll() throws BadAPICallException {
        return getAllEntities(VBD.class);
    }

    public boolean isBootable() {
        return bootable;
    }

    public void setBootable(boolean bootable) throws BadAPICallException {
        this.bootable = setter(bootable, "set_bootable");
    }

    public boolean isAttached() {
        return attached;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) throws BadAPICallException {
        this.mode = setter(mode, "set_mode");
    }

    public String getQosAlgorithm() {
        return qosAlgorithm;
    }

    public Map<String, String> getRuntimeProperties() {
        return runtimeProperties;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public Object[] getSupportedQosAlgorithms() {
        return supportedQosAlgorithms;
    }

    public int getDeviceIndex() {
        return value(deviceIndex, "get_userdevice");
    }

    public void setDeviceIndex(int deviceIndex) throws BadAPICallException {
        this.deviceIndex = setter(deviceIndex, "set_userdevice");
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) throws BadAPICallException {
        this.type = setter(type, "set_type");
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("attached", "currently_attached");
        map.put("deviceName", "device");
        map.put("deviceIndex", "userdevice");
        map.put("qosAlgorithm", "qos_algorithm_type");
        return map;
    }

    public static enum Mode {

        RO, RW
    }

    public static enum Type {

        CD, DISK
    }
}
