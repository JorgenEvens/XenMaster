/*
 * NamedEntity.java
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
package org.xenmaster.api.entity;

import java.util.HashMap;
import java.util.Map;

import org.xenmaster.controller.BadAPICallException;

/**
 * 
 * @created Oct 23, 2011
 * @author double-u
 */
public class NamedEntity extends XenApiEntity {

    @ConstructorArgument
    protected String name, description = "";

    public NamedEntity(String ref, boolean autoFill) {
        super(ref, autoFill);
    }

    public NamedEntity(String ref) {
        super(ref);
    }

    public NamedEntity() {
    }

    public String getDescription() {
        description = value(description, "get_name_description");
        return description;
    }

    public void setDescription(String description) throws BadAPICallException {
        this.description = setter(description, "set_name_description");
    }

    public String getName() {
        name = value(name, "get_name_label");
        return name;
    }

    public void setName(String name) throws BadAPICallException {
        this.name = setter(name, "set_name_label");
    }

    @Override
    protected Object dispatch(String methodName, Object... params) throws BadAPICallException {
        try {
            return super.dispatch(methodName, params);
        } catch (BadAPICallException ex) {
            ex.setOrigin(this);
            throw ex;
        }
    }

    @Override
    protected Map<String, String> interpretation() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "name_label");
        map.put("description", "name_description");
        return map;
    }
}
