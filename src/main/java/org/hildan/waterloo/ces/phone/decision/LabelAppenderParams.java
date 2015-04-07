package org.hildan.waterloo.ces.phone.decision;

public interface LabelAppenderParams {

    /**
     * Returns the relative or absolute path to the XML file containing the classifier to use to
     * label the dataset's entries.
     *
     * @return the decision tree XML file.
     */
    public String getClassifierFilePath();

    /**
     * Returns the relative or absolute path to the CSV file containing the dataset to label.
     *
     * @return the dataset CSV file.
     */
    public String getUnlabeledDatasetFilePath();

    /**
     * Returns the relative or absolute path to the CSV file to create, which will contain the
     * dataset with appended labels.
     *
     * @return the output CSV file.
     */
    public String getLabeledDatasetFilePath();

    /**
     * Whether the timestamps should be removed or not.
     *
     * @return {@code true} if the timestamps should be removed.
     */
    public boolean shouldRemoveTimestamps();

}
