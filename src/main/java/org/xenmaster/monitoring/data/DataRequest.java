/* DataRequest.java
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
package org.xenmaster.monitoring.data;

import org.joda.time.Period;

/**
 * 
 * @created Feb 18, 2012
 * @author double-u
 */
public class DataRequest {
    protected String name;
    protected String reference;
    protected String requestId;
    protected boolean vm;
    protected boolean updates;
    protected Period period;
    
    public DataRequest(String id) {
        this.requestId = id;
    }

    public String getName() {
        return name;
    }

    public Period getPeriod() {
        return period;
    }

    public String getReference() {
        return reference;
    }

    public boolean isUpdates() {
        return updates;
    }

    public boolean isVm() {
        return vm;
    }

    public String getRequestId() {
        return requestId;
    }
}
