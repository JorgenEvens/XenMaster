/* 
 * Correlator.java
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

import com.lmax.disruptor.EventHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.xenmaster.monitoring.data.DataKey;
import org.xenmaster.monitoring.data.RRDUpdates;
import org.xenmaster.monitoring.data.Record;

/**
 * 
 * @created Mar 1, 2012
 * @author double-u
 */
public class Correlator implements EventHandler<Record> {
    
    protected Distributor distrib;
    protected List<DataKey> keyRepository;
    protected List<CorrelatorActivityListener> cals;
    
    public Correlator() {
        distrib = new Distributor();
        keyRepository = new LinkedList<>();
        cals = new ArrayList<>();
    }
    
    @Override
    public void onEvent(Record event, long sequence, boolean endOfBatch) throws Exception {
        if (event.getLatestData() == null || event.getLatestData() == null) {
            return;
        }
        
        RRDUpdates ru = event.getLatestData();
        List<String> legend = ru.getMeta().getLegend();
        HashMap<DataKey, Double> distilled = new HashMap<>();
        // We're not interested in Min/Max values ATM, filter them out
        for (int i = 0; i < ru.getMeta().getLegend().size(); i++) {
            
            if (legend.get(i).startsWith("AVERAGE")) {
                DataKey key = getKeyForSignature(legend.get(i));
                Long recentKey = event.getLatestData().getData().keySet().iterator().next();
                ArrayList<Double> values = new ArrayList<>(event.getLatestData().getData().get(recentKey));
                distilled.put(key, values.get(i));
            }
        }
        
        distrib.dataUpdate(event.getLatestData().getData().keySet().iterator().next(), distilled);
        
        event.finishedHandling();
    }
    
    protected DataKey getKeyForSignature(String sign) {
        DataKey needle = DataKey.fromRRDKey(sign);
        int index = keyRepository.indexOf(needle);
        
        if (index == -1) {
            keyRepository.add(needle);
            for (CorrelatorActivityListener cal : cals) {
                cal.keyAdded(needle);
            }
            return needle;
        } else {
            return keyRepository.get(index);
        }
    }
    
    public Distributor getDistributor() {
        return distrib;
    }
    
    public static interface CorrelatorActivityListener {

        public void keyAdded(DataKey key);
    }
}
