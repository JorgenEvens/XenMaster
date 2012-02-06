/*
 * VM.java
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
package org.xenmaster.entities;

import java.util.UUID;

/**
 * 
 * @created Oct 9, 2011
 * @author double-u
 */
public class VM extends net.wgr.core.dao.Object {
    
    protected UUID id;
    

    @Override
    public String getColumnFamily() {
        return "vms";
    }

    @Override
    public String getKeyFieldName() {
        return "id";
    }
    
}
