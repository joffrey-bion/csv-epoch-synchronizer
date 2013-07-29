package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import java.util.HashMap;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Sample;
import com.joffrey_bion.utils.stats.FlowStats;

class K4b2SampleStats {
    
    private static final StatsColumn[] COLUMNS = StatsColumn.values();
    private HashMap<StatsColumn, FlowStats> stats;
    
    public K4b2SampleStats() {
        stats = new HashMap<>();
        for (StatsColumn c : COLUMNS) {
            stats.put(c, new FlowStats());
        }
    }
    
    public void add(Sample sample) {
        for (StatsColumn c : COLUMNS) {
            stats.get(c).add(sample.getValue(c.index), sample.duration);
        }
    }
    
    public void remove(Sample sample) {
        for (StatsColumn c : COLUMNS) {
            stats.get(c).remove(sample.getValue(c.index), sample.duration);
        }
    }
    
    public FlowStats getStats(StatsColumn c) {
        return stats.get(c);
    }
}
