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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xenmaster.api.Monitor;
import org.xenmaster.monitoring.data.DataKey;
import org.xenmaster.monitoring.data.DataRequest;

/**
 *
 * @created Feb 18, 2012
 * @author double-u
 */
public class Distributor {

    private List<Monitor> monitors;
    private List<DataRequest> requests;
    private List<DataListener> listeners;

    public Distributor() {
        this.monitors = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    public void dataUpdate(long timestamp, Map<DataKey, Double> data) {
        Map<DataRequest, Map<DataKey, Double>> assembly = new HashMap<>();

        for (Map.Entry<DataKey, Double> entry : data.entrySet()) {
            DataKey key = entry.getKey();

            // Map data lines on requests
            for (DataRequest request : requests) {
                if (request.match(key)) {
                    if (!assembly.containsKey(request)) {
                        assembly.put(request, new HashMap<DataKey, Double>());
                    }

                    assembly.get(request).put(entry.getKey(), entry.getValue());
                }
            }
        }

        for (Iterator<DataRequest> it = requests.iterator(); it.hasNext();) {
            DataRequest request = it.next();
            if (!handleDelivery(request, assembly.get(request))) {
                it.remove();
            }
        }
    }

    protected boolean handleDelivery(DataRequest dr, Map<DataKey, Double> data) {
        boolean updateAppreciated = false;

        for (Monitor m : monitors) {
            if (m.deliverUpdate(dr, data)) {
                updateAppreciated = true;
            }
        }

        for (DataListener dl : listeners) {
            dl.update(data);
        }

        if (!updateAppreciated && listeners.isEmpty()) {
            // The update wasn't appreciated, so remove the request for it
            return false;
        }

        return true;
    }

    public void postRequest(DataRequest dr) {
        this.requests.add(dr);
    }

    public void addListener(DataListener dl) {
        this.listeners.add(dl);
    }

    public boolean register(Monitor monitor) {
        this.monitors.add(monitor);
        return true;
    }

    public void deregister(Monitor monitor) {
        this.monitors.remove(monitor);
    }

    public abstract static class DataListener {

        public abstract void update(Map<DataKey, Double> data);
    }
}
