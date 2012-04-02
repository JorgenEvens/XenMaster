/* 
 * DataKey.java
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

import java.util.Objects;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @created Mar 4, 2012
 * @author double-u
 */
public class DataKey {

    protected String reference;
    protected String referencedType;
    protected Type type;
    protected String name;

    public DataKey(String reference, String referencedType, Type type, String name) {
        this.reference = reference;
        this.type = type;
        this.name = name;
        this.referencedType = referencedType;
    }

    public static DataKey fromRRDKey(String key) {
        String[] split = StringUtils.split(key, ':');
        return new DataKey(split[2], split[1], Type.valueOf(split[0]), split[3]);
    }

    public static enum Type {

        MINIMUM, MAXIMUM, AVERAGE
    }

    public String getName() {
        return name;
    }

    public String getReference() {
        return reference;
    }

    public Type getType() {
        return type;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataKey other = (DataKey) obj;
        if (!Objects.equals(this.reference, other.reference)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 7;
        return hash;
    }

    public String toString() {
        return type.name() + ':' + referencedType + ':' + reference + ':' + name;
    }
}
