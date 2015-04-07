package org.hildan.waterloo.ces.actigraph;

import org.hildan.utils.classification.Classifier;

/**
 * An enum containing different sets of cut points to determine a level of activity from a CPM
 * (counts per minute) number.
 *
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public enum CutPointsSet {

    /**
     * Custom set of cut points based on Freedson Adult VM3.
     */
    CUSTOM(new String[] {"Sedentary", "Light", "Moderate", "Vigorous"}, new double[] {150.0, 2690.0, 6166.0}),
    /**
     * Freedson Adult VM3 set of cut points.
     */
    FREEDSON(new String[] {"Light", "Moderate", "Vigorous", "VeryVigorous"}, new double[] {2690.0, 6166.0, 9642.0}),
    /**
     * Set of cut points containing only Sedentary and Active levels.
     */
    SEDENTARY_VS_ALL(new String[] {"Sedentary", "Active"}, new double[] {150.0});

    private final Classifier<String> classifier;

    /**
     * Creates a set of cut points that separate different levels by different thresholds.
     *
     * @param labels
     *            The labels of the different possible levels in this set of cut points.
     * @param thresholds
     *            The thresholds to decide between the levels. For all {@code i},
     *            {@code threshold[i]} is the limit between level {@code labels[i]} (just below the
     *            threshold) and {@code labels[i+1]} (just above the threshold).
     */
    private CutPointsSet(String[] labels, double[] thresholds) {
        classifier = new Classifier<>(labels, thresholds);
    }

    /**
     * Determines the level of activity corresponding to the specified CPM.
     *
     * @param countsPerMin
     *            The number of counts per minute to convert into a level of activity.
     * @return the name of the level of activity corresponding to the specified CPM, as a
     *         {@code String}.
     */
    public String countsToLevel(double countsPerMin) {
        return classifier.valueToLevel(countsPerMin);
    }

    /**
     * Return the possible levels of classification.
     *
     * @return the possible levels of classification.
     */
    public String[] getLevels() {
        return classifier.getLevels();
    }
}
