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

import com.google.gson.Gson;
import net.wgr.server.web.handling.WebCommandHandler;
import net.wgr.wcp.command.Command;
import net.wgr.wcp.connectivity.Connection;
import org.xenmaster.monitoring.MonitoringAgent;
import org.xenmaster.monitoring.data.DataRequest;

/**
 * 
 * @created Feb 17, 2012
 * @author double-u
 */
public class Monitor extends WebCommandHandler {

    protected static Gson gson = new Gson();

    public Monitor() {
        super("monitoring");
    }

    @Override
    public Object execute(Command cmd) {
        if (cmd.getName().equals("requestData")) {
            requestData(gson.fromJson(cmd.getData(), DataRequest.class), cmd.getConnection());
        }
        return null;
    }

    public final void requestData(DataRequest req, Connection conn) {
        MonitoringAgent.get().getCorrelator().getDistributor().serveRequest(req, conn);
    }
}
