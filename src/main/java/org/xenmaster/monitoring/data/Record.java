/*
 * Record.java
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
package org.xenmaster.monitoring.data;

import java.util.Collection;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.xenmaster.monitoring.engine.Slot;

import com.lmax.disruptor.EventFactory;

/**
 * 
 * @created Oct 6, 2011
 * @author double-u
 */
public class Record {

    protected double cpuUsage;
    protected int memoryUsage;
    protected int memoryTotal;
    protected String reference;
    protected String xml;
    protected boolean vm;
    protected RRD rawData;
    protected Slot slot;

    public Record(float CPUusage, int memoryUsage, int memoryTotal, String ref, boolean isVM) {
        this.cpuUsage = CPUusage;
        this.memoryTotal = memoryTotal;
        this.memoryUsage = memoryUsage;
        this.reference = ref;
        this.vm = isVM;
    }

    public Record() {
    }

    public Record(String ref, boolean isVM) {
        this.reference = ref;
        this.vm = isVM;
    }
    
    public final static EventFactory<Record> EVENT_FACTORY = new EventFactory<Record>() {

        @Override
        public Record newInstance() {
            return new Record();
        }
    };
    
    public void attachSlot(Slot s) {
        this.slot = s;
    }

    public RRD getRawData() {
        return rawData;
    }

    public void setRawData(RRD rawData) {
        this.rawData = rawData;
    }

    public String getReference() {
        return reference;
    }

    protected final void applyStatistics(Collection<Double> values) {
        // Let's get statistical
        DescriptiveStatistics ds = new DescriptiveStatistics();

        for (double util : values) {
            ds.addValue(util);
        }

        cpuUsage = ds.getMean();
        double stdDev = ds.getStandardDeviation();

        // TODO: actually test this and generate warning
        // Check if all vCPUs have a fair load, e.g. [45, 60, 50] would be fair, [90, 4, 2] indicates you should learn threading
        if (stdDev > 0.8) {
            Logger.getLogger(getClass()).info((vm ? "VM" : "Host") + " " + reference + " has an unfair load distribution");
        }

        if (stdDev > 0) {
            try {
                NormalDistributionImpl ndi = new NormalDistributionImpl(ds.getMean(), stdDev);
                double cp = ndi.cumulativeProbability(90);
                if (cp > 0.8) {
                    // 80% of the CPUs have a >90% load
                    // TODO warning
                    Logger.getLogger(getClass()).info((vm ? "VM" : "Host") + " " + reference + " has a load >=90% on 80% of the available CPUs");
                }
            } catch (MathException ex) {
                Logger.getLogger(getClass()).error("Flawed maths", ex);
            }
        }
    }

    public boolean isVM() {
        return vm;
    }

    public String getXML() {
        return xml;
    }
    
    public void setXML(String xml) {
        this.xml = xml;
    }
}
