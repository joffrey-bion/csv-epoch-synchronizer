package com.joffrey_bion.csv.csv_epoch_synchronizer;
public class InstanceProperties {

    private static final long WINDOW_WIDTH_SEC = 1;
    private static final long EPOCH_WIDTH_SEC = 1;

    /** Window width in nanoseconds */
    public static final long WINDOW_WIDTH_NANO = WINDOW_WIDTH_SEC * 1000 * 1000000;
    /** Epochs width in nanoseconds */
    public static final long EPOCH_WIDTH_NANO = EPOCH_WIDTH_SEC * 1000 * 1000000;
    /** Time between the beginning of the window and the beginning of the epoch */
    public static final long WINBEGIN_TO_EPBEGIN = (WINDOW_WIDTH_NANO - EPOCH_WIDTH_NANO) / 2;
    /** Start time in actigraph reference in nanoseconds */
    public long startTime;
    /** Stop time in actigraph reference in nanoseconds */
    public long stopTime;
    /**
     * Delay in nanoseconds to add to a phone timestamp to find the corresponding
     * actigraph timestamp.
     */
    public long delay;

}
