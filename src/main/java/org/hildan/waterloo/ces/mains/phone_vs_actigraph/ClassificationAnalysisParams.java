package org.hildan.waterloo.ces.mains.phone_vs_actigraph;

import java.util.List;

public interface ClassificationAnalysisParams {

    /**
     * Returns the path to the CSV file containing both the true labels and the classification
     * labels.
     *
     * @return the path to the CSV file containing both the true labels and the classification
     *         labels.
     */
    public String getTwoLabeledFile();

    /**
     * Returns the header of the column containing the classification labels.
     *
     * @return the header of the column containing the classification labels.
     */
    public String getHeaderClassifiedAs();

    /**
     * Returns the header of the column containing the true labels.
     *
     * @return the header of the column containing the true labels.
     */
    public String getHeaderTruth();

    /**
     * Returns the possible classification levels.
     *
     * @return the possible classification levels.
     */
    public List<String> getLevels();

}
