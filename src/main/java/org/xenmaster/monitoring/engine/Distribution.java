/* Distribution.java
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
import java.util.Map;
import net.wgr.wcp.Commander;
import net.wgr.wcp.Scope;
import net.wgr.wcp.command.Command;
import net.wgr.wcp.connectivity.Connection;
import org.xenmaster.monitoring.data.DataRequest;

/**
 * 
 * @created Feb 18, 2012
 * @author double-u
 */
public class Distribution {

    protected Map<DataRequest, Connection> subscribers;

    public Distribution() {
        subscribers = new HashMap<>();
    }

    public void dataUpdate(long timestamp, Map<String, Double> data) {
        HashMap<Connection, Map<String, Double>> accumulate = new HashMap<>();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            String dataLabel = entry.getKey();
            for (Map.Entry<DataRequest, Connection> request : subscribers.entrySet()) {
                if (request.getKey().getName().equals(dataLabel)) {
                    if (accumulate.containsKey(request.getValue())) {
                        accumulate.get(request.getValue()).putAll(data);
                    } else {
                        accumulate.put(request.getValue(), data);
                    }
                }
            }
        }
    }
    
    public void serveRequest(DataRequest dc, Connection c) {
        this.subscribers.put(dc, c);
    }

    protected void distribute(Map<Connection, Map<String, Double>> data) {
        Command cmd = null;
        for (Map.Entry<Connection, Map<String, Double>> entry : data.entrySet()) {
            cmd = new Command("monitoring", "update", entry.getValue());
            Scope scope = new Scope(entry.getKey().getId());
            Commander.getInstance().commandeer(cmd, scope);
        }
    }
}
