package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import java.util.HashMap;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.K4b2Sample;
import com.joffrey_bion.utils.stats.FlowStats;

class K4b2SampleStats {
    
    private static final K4b2StatsColumn[] COLUMNS = K4b2StatsColumn.values();
    private HashMap<K4b2StatsColumn, FlowStats> stats;
    
    public K4b2SampleStats() {
        stats = new HashMap<>();
        for (K4b2StatsColumn c : COLUMNS) {
            stats.put(c, new FlowStats());
        }
    }
    
    public void add(K4b2Sample sample) {
        for (K4b2StatsColumn c : COLUMNS) {
            stats.get(c).add(sample.getValue(c.index), sample.duration);
        }
    }
    
    public void remove(K4b2Sample sample) {
        for (K4b2StatsColumn c : COLUMNS) {
            stats.get(c).remove(sample.getValue(c.index), sample.duration);
        }
    }
    
    public FlowStats getStats(K4b2StatsColumn c) {
        return stats.get(c);
    }
}
