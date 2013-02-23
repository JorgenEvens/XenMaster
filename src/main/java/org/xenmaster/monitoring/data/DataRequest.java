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

import java.util.List;
import java.util.UUID;
import org.joda.time.Period;

/**
 *
 * @created Feb 18, 2012
 * @author double-u
 */
public class DataRequest {

    protected List<String> keys;
    protected DefaultKeySets keySet;
    protected UUID id;
    protected boolean vm;
    protected boolean updates;
    protected Period period;

    public DataRequest() {
    }

    public DataRequest(UUID id, boolean isVM, DefaultKeySets keySet) {
        this.id = id;
        this.vm = isVM;
        this.keySet = keySet;
    }

    public List<String> getKeys() {
        return keys;
    }

    public Period getPeriod() {
        return period;
    }

    public UUID getId() {
        return id;
    }

    public boolean isUpdates() {
        return updates;
    }

    public boolean isVm() {
        return vm;
    }

    /**
     * Checks if the key matches this request
     * @param datakey
     * @return
     */
    public boolean match(DataKey datakey) {
        if (!datakey.getId().equals(id)) {
            return false;
        }

        if (keySet != null) {
            switch (keySet) {
                case ALL:
                    return true;
                case CPU:
                    return datakey.getName().contains("cpu");
                case PIF:
                    return datakey.getName().contains("pif");
                case XAPI:
                    return datakey.getName().contains("xapi");
            }
        }

        return keys != null && keys.contains(datakey.getName());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataRequest other = (DataRequest) obj;
        if (this.isVm() != other.isVm()) {
            return false;
        }
        if (!this.getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    public static enum DefaultKeySets {

        ALL, XAPI, PIF, CPU
    }
}
