package com.joffrey_bion.csv.csv_epoch_synchronizer;

import java.util.LinkedList;

public class CountsLabeler {

    public static final int CUSTOM_VM3 = 0;
    public static final int FREEDSON_ADULT_VM3 = 1;
    public static final int SEDENTARY_VS_ALL = 2;

    private class CutPoint {
        public String label;
        public double upperLimit;

        public CutPoint(String label, double upperLimit) {
            this.label = label;
            this.upperLimit = upperLimit;
        }
    }

    private LinkedList<CutPoint> cpmCutPoints;

    public CountsLabeler(int cutPointsSetIndex) {
        cpmCutPoints = new LinkedList<>();
        // the names of the levels of activity have to match the camelcase value of
        // the Level enum in the phone app
        if (cutPointsSetIndex == CUSTOM_VM3) {
            cpmCutPoints.add(new CutPoint("Sedentary", 150.0));
            cpmCutPoints.add(new CutPoint("Light", 2690.0));
            cpmCutPoints.add(new CutPoint("Moderate", 6166.0));
            cpmCutPoints.add(new CutPoint("Vigorous", Double.MAX_VALUE));
        } else if (cutPointsSetIndex == FREEDSON_ADULT_VM3) {
            cpmCutPoints.add(new CutPoint("Light", 2690.0));
            cpmCutPoints.add(new CutPoint("Moderate", 6166.0));
            cpmCutPoints.add(new CutPoint("Vigorous", 9642.0));
            cpmCutPoints.add(new CutPoint("VeryVigorous", Double.MAX_VALUE));
        } else if (cutPointsSetIndex == SEDENTARY_VS_ALL) {
            cpmCutPoints.add(new CutPoint("Sedentary", 150.0));
            cpmCutPoints.add(new CutPoint("Active", Double.MAX_VALUE));
        } else {
            throw new RuntimeException("unknown cut points set");
        }
    }

    public String countsToLabel(double countsPerMin) {
        if (countsPerMin < 0)
            throw new IllegalArgumentException("CPM must be positive");
        for (CutPoint cutPoint : cpmCutPoints) {
            if (countsPerMin <= cutPoint.upperLimit) {
                return cutPoint.label;
            }
        }
        throw new RuntimeException("No label matched " + countsPerMin + " CPM.");
    }

}
