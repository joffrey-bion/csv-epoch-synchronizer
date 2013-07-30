package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import java.util.HashMap;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Sample;
import com.joffrey_bion.utils.stats.FlowStats;

/**
 * An equivalent of {@link FlowStats} that manages all the values in a K4b2
 * {@link Sample} at the same time.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
class K4b2SampleStats {

    private static final StatsColumn[] COLUMNS = StatsColumn.values();
    private HashMap<StatsColumn, FlowStats> stats;

    /**
     * Creates a new {@code K4b2SampleStats}.
     */
    public K4b2SampleStats() {
        stats = new HashMap<>();
        for (StatsColumn c : COLUMNS) {
            stats.put(c, new FlowStats());
        }
    }

    /**
     * Adds the specified sample to this statistics object, with a weight equal to
     * its duration.
     * 
     * @param sample
     *            The {@link Sample} to add.
     */
    public void add(Sample sample) {
        for (StatsColumn c : COLUMNS) {
            stats.get(c).add(sample.getValue(c.index), sample.duration);
        }
    }

    /**
     * Removes the specified sample from this statistics object, with a weight equal
     * to its duration.
     * 
     * @param sample
     *            The {@link Sample} to remove.
     */
    public void remove(Sample sample) {
        for (StatsColumn c : COLUMNS) {
            stats.get(c).remove(sample.getValue(c.index), sample.duration);
        }
    }

    /**
     * Returns the {@link FlowStats} object for the specified column.
     * 
     * @param c
     *            The column to get the stats from.
     * @return the {@link FlowStats} object for the specified column.
     */
    public FlowStats getStats(StatsColumn c) {
        return stats.get(c);
    }
}
