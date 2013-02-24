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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.wgr.wcp.Commander;
import net.wgr.wcp.Scope;
import net.wgr.wcp.command.Command;
import net.wgr.wcp.connectivity.Connection;
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

    protected List<DataRequest> requests;
    protected boolean registered;

    public Monitor(Connection connection) {
        super(connection);

        this.requests = new ArrayList<>();
    }

    public void requestData(DataRequest req) {
        if (!registered) {
            registered = MonitoringAgent.get().getCorrelator().getDistributor().register(this);
        }
        this.requests.add(req);
        MonitoringAgent.get().getCorrelator().getDistributor().postRequest(req);
    }

    public boolean deliverUpdate(DataRequest req, Map<DataKey, Double> values) {
        for (DataRequest dr : requests) {
            if (dr.equals(req)) {
                DataUpdate du = new DataUpdate(reference, values);
                Command cmd = new Command("monitoring", "update", du);
                Scope scope = new Scope(connection.getId());
                Commander.get().commandeer(cmd, scope);

                return true;
            }
        }

        return false;
    }
    
    public void cancel() {
        requests.clear();
    }

    public void cancel(DataRequest dr) {
        for (Iterator<DataRequest> it = requests.iterator(); it.hasNext();) {
            DataRequest req = it.next();
            if (req.equals(dr)) {
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
}
