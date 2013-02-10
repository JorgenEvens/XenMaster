/* Distributor.java
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
package org.xenmaster.monitoring.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wgr.wcp.Commander;
import net.wgr.wcp.Scope;
import net.wgr.wcp.command.Command;
import net.wgr.wcp.connectivity.Connection;
import org.xenmaster.monitoring.data.DataKey;
import org.xenmaster.monitoring.data.DataRequest;

/**
 * 
 * @created Feb 18, 2012
 * @author double-u
 */
public class Distributor {

    private Map<DataRequest, DataClient> subscribers;

    public Distributor() {
        subscribers = new HashMap<>();
    }

    public void dataUpdate(long timestamp, Map<DataKey, Double> data) {
        HashMap<DataClient, Map<DataKey, Double>> accumulate = new HashMap<>();
        for (Map.Entry<DataKey, Double> entry : data.entrySet()) {
            DataKey key = entry.getKey();
            for (Map.Entry<DataRequest, DataClient> request : subscribers.entrySet()) {
                if (request.getKey().match(key)) {
                    if (!accumulate.containsKey(request.getValue())) {
                        accumulate.put(request.getValue(), new HashMap<DataKey, Double>());
                    }

                    accumulate.get(request.getValue()).put(key, entry.getValue());
                }
            }
        }

        distribute(accumulate);
    }

    public void serveRequest(DataRequest dr, Connection c) {
        DataClient dc = new DataClient();
        dc.connection = c;
        this.subscribers.put(dr, dc);
    }
    
    public void serveRequest(DataRequest dr, DataListener dl) {
        DataClient dc = new DataClient();
        dc.listener = dl;
        this.subscribers.put(dr, dc);
    }

    private void distribute(Map<DataClient, Map<DataKey, Double>> data) {
        Command cmd = null;
        for (Map.Entry<DataClient, Map<DataKey, Double>> entry : data.entrySet()) {
            if (entry.getKey().connection != null) {
                cmd = new Command("monitoring", "update", entry.getValue());
                Scope scope = new Scope(entry.getKey().connection.getId());
                Commander.get().commandeer(cmd, scope);
            } else if (entry.getKey().listener != null) {
                entry.getKey().listener.update(entry.getValue());
            }
        }
    }

    private static class DataClient {

        public Connection connection;
        public DataListener listener;
    }

    public abstract static class DataListener {

        protected List<DataRequest> requests;

        public abstract void update(Map<DataKey, Double> data);
    }
}
