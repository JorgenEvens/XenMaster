/* Monitor.java
 * Copyright (C) 2012 Wannes De Smet
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.xenmaster.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.wgr.wcp.Commander;
import net.wgr.wcp.Scope;
import net.wgr.wcp.command.Command;
import net.wgr.wcp.connectivity.Connection;
import org.apache.commons.lang.StringUtils;
import org.xenmaster.api.entity.VIF;
import org.xenmaster.api.entity.VM;
import org.xenmaster.api.util.APIHook;
import org.xenmaster.monitoring.MonitoringAgent;
import org.xenmaster.monitoring.data.DataKey;
import org.xenmaster.monitoring.data.DataRequest;

/**
 *
 * @created Feb 17, 2012
 * @author double-u
 */
public class Monitor extends APIHook {

    protected Map<Integer, DataRequest> requests;
    protected boolean registered;
    protected boolean describeRecords;
    protected List<Integer> describedRecords;

    public Monitor(Connection connection) {
        super(connection);

        this.requests = new HashMap<>();
    }

    public int requestData(DataRequest req) {
        if (!registered) {
            registered = MonitoringAgent.get().getCorrelator().getDistributor().register(this);
        }
        int idx = MonitoringAgent.get().getCorrelator().getDistributor().postRequest(req);
        this.requests.put(idx, req);
        return idx;
    }

    public boolean deliverUpdate(DataRequest req, Map<DataKey, Double> values) {
        for (Entry<Integer, DataRequest> entry : requests.entrySet()) {
            if (entry.getValue().equals(req)) {
                DataUpdate du = new DataUpdate(reference, values);
                Command cmd = new Command("monitoring", "update", du);
                Scope scope = new Scope(connection.getId());
                Commander.get().commandeer(cmd, scope);

                if (!describeRecords) {
                    describeKeys(values.keySet());
                }

                return true;
            }
        }

        return false;
    }

    public void describeRecords() {
        this.describedRecords = new ArrayList<>();
        this.describeRecords = true;
    }

    protected void describeKeys(Collection<DataKey> keys) {
        HashMap<DataKey, DataKeyDescription> description = new HashMap<>();

        // temps keeping us from having to resolve objects
        VM vm = null;
        ArrayList<VIF> vifs = new ArrayList<>();

        for (DataKey key : keys) {
            String[] ks = StringUtils.split(key.getName(), '_');

            if (vm == null || !vm.getIDString().equals(key.getId().toString())) {
                vm = new VM(key.getId(), true);

                vifs.clear();
                vifs.addAll(vm.getVIFs());
            }

            if (ks[0].equals("vif")) {
                VIF vif = vifs.get(Integer.parseInt(ks[1]));
                description.put(key, new DataKeyDescription(vif.getReference(), 
                        "NIC: " + vif.getNetwork().getName() + '_' + ks[2]));
            }
        }

        Command cmd = new Command("monitoring", "description", description);
        Scope scope = new Scope(connection.getId());
        Commander.get().commandeer(cmd, scope);
        
        describeRecords = false;
    }

    public void cancel() {
        requests.clear();
    }

    public void cancel(DataRequest dr) {
        for (Iterator<Entry<Integer, DataRequest>> it = requests.entrySet().iterator(); it.hasNext();) {
            Entry<Integer, DataRequest> req = it.next();
            if (req.getValue().equals(dr)) {
                it.remove();
            }
        }
    }

    protected static class DataUpdate {

        public String reference;
        public Map<DataKey, Double> data;

        public DataUpdate(String reference, Map<DataKey, Double> data) {
            this.reference = reference;
            this.data = data;
        }
    }

    protected static class DataKeyDescription {

        public String reference;
        public String friendlyName;

        public DataKeyDescription(String reference, String friendlyName) {
            this.reference = reference;
            this.friendlyName = friendlyName;
        }
    }
}
