package com.joffrey_bion.csv_epoch_synchronizer.phone;

public interface PhoneRawToEpParams {

    /**
     * Returns the timestamp when the accumulation should start, in the phone
     * reference. It corresponds to the beginning of the output file.
     * 
     * @return the timestamp when the accumulation should start.
     */
    public long getPhoneStartTime();

    /**
     * Returns the timestamp when the accumulation should stop, in the phone
     * reference. It corresponds to the end of the output file.
     * 
     * @return the timestamp when the accumulation should stop.
     */
    public long getPhoneStopTime();

    /**
     * Returns the delay in milliseconds to add to a phone time to find the target
     * time.
     * 
     * @return the delay between the phone time and the target time.
     */
    public long getDelay();

    /**
     * Returns the width of the phone time window in nanoseconds. The time window is
     * the set of samples that are used to calculate statistics in order to
     * accumulate these samples into an epoch.
     * 
     * @return the width of the phone time window in nanoseconds.
     */
    public long getWindowWidthNano();

    /**
     * Returns the width of the phone epochs in nanoseconds. The epochs are the
     * periods of time in the center of each time window that are associated with the
     * accumulated values over this window. The epochs do not overlap, unlike the
     * time windows.
     * 
     * @return the width of the phone epochs in nanoseconds.
     */
    public long getEpochWidthNano();

    /**
     * Returns the delay between the beginning of a time window and the beginning of
     * the corresponding epoch.
     * 
     * @return the delay between the beginning of a time window and the beginning of
     *         the corresponding epoch.
     */
    public long getWinBeginToEpBegin();

}
