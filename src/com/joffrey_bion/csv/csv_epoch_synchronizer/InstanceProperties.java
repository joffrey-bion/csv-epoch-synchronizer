package com.joffrey_bion.csv.csv_epoch_synchronizer;

public class InstanceProperties {

    private static final long DEFAULT_WINDOW_WIDTH_SEC = 1;
    private static final long DEFAULT_EPOCH_WIDTH_SEC = 1;

    /** Window width in seconds */
    private long windowWidthSec;
    /** Epochs width in seconds */
    private long epochWidthSec;
    /** Window width in nanoseconds */
    private long windowWidthNano;
    /** Epochs width in nanoseconds */
    private long epochWidthNano;
    /** Time between the beginning of the window and the beginning of the epoch */
    private long winBeginToEpBegin;

    /** Name of the file containing the raw data from the phone */
    public String phoneRawFilename;
    /** Name of the file containing the epochs from the actigraph */
    public String actigraphEpFilename;
    /** Name of the output file */
    public String outputFilename = "dataset.csv";
    /** Start time in actigraph reference in nanoseconds */
    public long startTime;
    /** Stop time in actigraph reference in nanoseconds */
    public long stopTime;
    /**
     * Delay in nanoseconds to add to a phone timestamp to find the corresponding
     * actigraph timestamp.
     */
    public long delay;

    public InstanceProperties() {
        windowWidthSec = DEFAULT_WINDOW_WIDTH_SEC;
        epochWidthSec = DEFAULT_EPOCH_WIDTH_SEC;
    }

    public void setWindowWidth(int windowWidthSec) {
        this.windowWidthSec = windowWidthSec;
        updateFields();
    }

    public void setEpochWidth(int epochWidthSec) {
        this.epochWidthSec = epochWidthSec;
        updateFields();
    }

    private void updateFields() {
        windowWidthNano = windowWidthSec * 1000 * 1000000;
        epochWidthNano = epochWidthSec * 1000 * 1000000;
        winBeginToEpBegin = (windowWidthNano - epochWidthNano) / 2;
    }

    public long getEpochWidthNano() {
        return epochWidthNano;
    }

    public long getWindowWidthNano() {
        return windowWidthNano;
    }

    public long getWinBeginToEpBegin() {
        return winBeginToEpBegin;
    }

}
