/* RRDUpdates.java
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

import com.google.common.collect.Multimap;
import com.thoughtworks.xstream.XStream;
import java.io.InputStream;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @created Feb 19, 2012
 *
 * @author double-u
 */
public class RRDUpdates {

    protected Meta meta;
    protected Multimap<Long, Double> data;

    public Multimap<Long, Double> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }

    public static class Meta {

        protected long start;
        protected int step;
        protected long end;
        protected int rows, columns;
        protected List<String> legend;

        public int getColumns() {
            return columns;
        }

        public long getEnd() {
            return end;
        }

        public List<String> getLegend() {
            return legend;
        }

        public int getRows() {
            return rows;
        }

        public long getStart() {
            return start;
        }

        public int getStep() {
            return step;
        }
    }

    public static RRDUpdates parse(InputStream xmlStream) {
        XStream xs = new XStream();
        xs.alias("xport", RRDUpdates.class);
        xs.alias("meta", Meta.class);
        xs.aliasType("entry", String.class);
        xs.aliasType("data", Multimap.class);
        xs.registerLocalConverter(RRDUpdates.class, "data", new RRDUpdateConverter());

        try {
            RRDUpdates fromXML = (RRDUpdates) xs.fromXML(xmlStream);
            return fromXML;
        }
        catch (RuntimeException ex) {
            Logger.getLogger(RRDUpdates.class).error("Failed to parse RRD updates", ex);
            return null;
        }
    }
}
