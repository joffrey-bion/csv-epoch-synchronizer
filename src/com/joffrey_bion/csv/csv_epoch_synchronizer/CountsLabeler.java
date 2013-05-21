package com.joffrey_bion.csv.csv_epoch_synchronizer;
import java.util.LinkedList;

public class CountsLabeler {

    public static final int FREEDSON_ADULT_VM3 = 0;

    private class CutPoint {
        public String label;
        public double upperLimit;

        public CutPoint(String label, double upperLimit) {
            this.label = label;
            this.upperLimit = upperLimit;
        }
    }

    private LinkedList<CutPoint> cutPoints;

    public CountsLabeler() {
        setCutPoints(FREEDSON_ADULT_VM3);
    }

    private void setCutPoints(int setIndex) {
        cutPoints = new LinkedList<>();
        if (setIndex == FREEDSON_ADULT_VM3) {
            cutPoints.add(new CutPoint("Light", 2690.0));
            cutPoints.add(new CutPoint("Moderate", 6166.0));
            cutPoints.add(new CutPoint("Vigorous", 9642.0));
        } else {
            throw new RuntimeException("unknown cut points set");
        }
        cutPoints.add(new CutPoint("Very vigorous", Double.MAX_VALUE));
    }

    public String countsToLabel(double countsPerMin) {
        if (countsPerMin < 0)
            throw new IllegalArgumentException("CPM must be positive");
        for (CutPoint cutPoint : cutPoints) {
            if (countsPerMin <= cutPoint.upperLimit) {
                return cutPoint.label;
            }
        }
        throw new RuntimeException("No label matched " + countsPerMin + " CPM.");
    }

}
