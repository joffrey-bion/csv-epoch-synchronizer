package com.joffrey_bion.csv_epoch_synchronizer.k4b2;

import com.joffrey_bion.utils.classifier.Classifier;

public enum METsToLevel {
    /**
     * Standard chart converting METs into levels of activity.
     */
    STANDARD(new String[] { "Sedentary", "Light", "Moderate", "Vigorous" }, new double[] { 1.5,
            2.9, 5.9 });

    private final Classifier<String> classifier;
  
    /**
     * Creates a set of cut points that separate different levels by different
     * thresholds.
     * 
     * @param labels
     *            The labels of the different possible levels in this set of cut
     *            points.
     * @param thresholds
     *            The thresholds to decide between the levels. For all {@code i},
     *            {@code threshold[i]} is the limit between level {@code labels[i]}
     *            (just below the threshold) and {@code labels[i+1]} (just above the
     *            threshold).
     */
    private METsToLevel(String[] labels, double[] thresholds) {
        classifier = new Classifier<>(labels, thresholds);
    }

    /**
     * Determines the level of activity corresponding to the specified METs number.
     * 
     * @param METs
     *            The METs to convert into a level of activity.
     * @return the name of the level of activity corresponding to the specified METs,
     *         as a {@code String}.
     */
    public String metsToLevel(double METs) {
        return classifier.valueToLevel(METs);
    }
}