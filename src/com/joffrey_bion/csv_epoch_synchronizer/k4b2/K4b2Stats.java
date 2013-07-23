package com.joffrey_bion.csv_epoch_synchronizer.k4b2;

import java.util.HashMap;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats.PhaseResults;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats.RestingResults;

@SuppressWarnings("serial")
public class K4b2Stats extends HashMap<Phase, PhaseResults> {

    @Override
    public PhaseResults put(Phase phase, PhaseResults results) {
        if (phase == Phase.RESTING && !(results instanceof RestingResults)) {
            throw new IllegalArgumentException("The resting must be assigned resting results.");
        }
        return super.put(phase, results);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Phase p : Phase.values()) {
            sb.append(p);
            sb.append(" ");
            sb.append(get(p));
            sb.append("\n");
        }
        return sb.toString();
    }
    
}
