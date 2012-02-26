/* RRDUpdateConverter.java
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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * 
 * @created Feb 25, 2012
 * @author double-u
 */
public class RRDUpdateConverter implements Converter {

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        if (!reader.getNodeName().equals("data")) {
            throw new IllegalArgumentException("XML must be xport->data");
        }

        Multimap<Long, Double> data = LinkedListMultimap.create();
        reader.moveDown();
        while (reader.getNodeName().equals("row")) {
            // Move to t node
            reader.moveDown();
            long time = Long.parseLong(reader.getValue());
            reader.moveUp();
            // Mode to v nodes
            reader.moveDown();
            while (reader.getNodeName().equals("v")) {
                data.put(time, Double.parseDouble(reader.getValue()));
                reader.moveUp();
                
                if (reader.hasMoreChildren()) {
                    reader.moveDown();
                } else {
                    break;
                }
            }
            reader.moveUp();
        }
        return data;
    }

    @Override
    public boolean canConvert(Class type) {
        // convert ALL the types!
        return true;
    }
}
