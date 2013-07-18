package com.joffrey_bion.csv_epoch_synchronizer.k4b2;

import com.joffrey_bion.utils.stats.FlowStats;

class NestedStats {

    private FlowStats meansGlobalStats;
    private FlowStats currentGroupStats;

    public NestedStats() {
        meansGlobalStats = new FlowStats();
        currentGroupStats = new FlowStats();
    }

    public void add(double value, double weight) {
        currentGroupStats.add(value, weight);
    }

    public void finishCurrentGroup() {
        meansGlobalStats.add(currentGroupStats.mean(), currentGroupStats.getTotalWeight());
        currentGroupStats.clear();
    }

    public FlowStats getGlobalStats() {
        return meansGlobalStats;
    }
}
