package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import java.util.HashMap;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.K4b2CsvReader;
import com.joffrey_bion.utils.stats.FlowStats;

public class K4b2LineStats {
    
    private static final Column[] COLUMNS = Column.values();
    private HashMap<Column, FlowStats> stats;
    
    public K4b2LineStats() {
        stats = new HashMap<>();
        for (Column c : COLUMNS) {
            stats.put(c, new FlowStats());
        }
    }
    
    public void add(K4b2Line line) {
        for (Column c : COLUMNS) {
            stats.get(c).add(K4b2CsvReader.getValue(c.index, line.cols), line.duration);
        }
    }
    
    public void remove(K4b2Line line) {
        for (Column c : COLUMNS) {
            stats.get(c).remove(K4b2CsvReader.getValue(c.index, line.cols), line.duration);
        }
    }
    
    public FlowStats getStats(Column c) {
        return stats.get(c);
    }
}
