package com.joffrey_bion.csv_epoch_synchronizer.actigraph;

import java.util.LinkedList;

public enum CutPointsSet {

    /**
     * Custom set of cut points based on Freedson Adult VM3.
     */
    CUSTOM(new String[] { "Sedentary", "Light", "Moderate", "Vigorous" }, new double[] { 150.0,
            2690.0, 6166.0 }),
    /**
     * Freedson Adult VM3 set of cut points.
     */
    FREEDSON(new String[] { "Light", "Moderate", "Vigorous", "VeryVigorous" }, new double[] {
            2690.0, 6166.0, 9642.0 }),
    /**
     * Set of cut points containing only Sedentary and Active levels.
     */
    SEDENTARY_VS_ALL(new String[] { "Sedentary", "Active" }, new double[] { 150.0 });

    private LinkedList<CutPoint> cpmCutPoints;

    private class CutPoint {
        public String label;
        public double upperLimit;

        public CutPoint(String label, double upperLimit) {
            this.label = label;
            this.upperLimit = upperLimit;
        }
    }

    private CutPointsSet(String[] labels, double[] values) {
        if (labels.length != values.length + 1) {
            throw new IllegalArgumentException("There must be n-1 values to separate n labels.");
        }
        this.cpmCutPoints = new LinkedList<>();
        for (int i = 0; i < values.length; i++) {
            this.cpmCutPoints.add(new CutPoint(labels[i], values[i]));
        }
        this.cpmCutPoints.add(new CutPoint(labels[labels.length - 1], Double.MAX_VALUE));
    }

    public String countsToLevel(double countsPerMin) {
        if (countsPerMin < 0)
            throw new IllegalArgumentException("CPM must be positive");
        for (CutPoint cutPoint : cpmCutPoints) {
            if (countsPerMin <= cutPoint.upperLimit) {
                return cutPoint.label;
            }
        }
        throw new RuntimeException("Internal error: no label matched " + countsPerMin + " CPM.");
    }
}
