package org.hildan.waterloo.ces.phone;

public interface PhoneRawToEpParams {

    /**
     * Returns the relative or absolute path to the CSV file containing the raw samples from the
     * phone.
     *
     * @return the relative or absolute path to the CSV file containing the raw samples from the
     *         phone.
     */
    public String getInputFilePath();

    /**
     * Returns the relative or absolute path to the CSV file to create with the epoch-accumulated
     * data.
     *
     * @return the relative or absolute path to the CSV file to create with the epoch-accumulated
     *         data.
     */
    public String getPhoneEpochFilePath();

    /**
     * Returns the timestamp when the accumulation should start, in the phone reference. It
     * corresponds to the beginning of the output file.
     *
     * @return the timestamp when the accumulation should start, in nanoseconds.
     */
    public long getPhoneStartTimeNano();

    /**
     * Returns the timestamp when the accumulation should stop, in the phone reference. It
     * corresponds to the end of the output file.
     *
     * @return the timestamp when the accumulation should stop, in nanoseconds.
     */
    public long getPhoneStopTimeNano();

    /**
     * Returns the delay in nanoseconds to add to a phone time to find the target time.
     *
     * @return the delay between the phone time and the target time.
     */
    public long getDelayNano();

    /**
     * Returns the width of the phone time window in nanoseconds. The time window is the set of
     * samples that are used to calculate statistics in order to accumulate these samples into an
     * epoch.
     *
     * @return the width of the phone time window in nanoseconds.
     */
    public long getWindowWidthNano();

    /**
     * Returns the width of the phone epochs in nanoseconds. The epochs are the periods of time in
     * the center of each time window that are associated with the accumulated values over this
     * window. The epochs do not overlap, unlike the time windows.
     *
     * @return the width of the phone epochs in nanoseconds.
     */
    public long getEpochWidthNano();

    /**
     * Returns the delay in nanoseconds between the beginning of a time window and the beginning of
     * the corresponding epoch.
     *
     * @return the delay in nanoseconds between the beginning of a time window and the beginning of
     *         the corresponding epoch.
     */
    public long getWinBeginToEpBegin();

}
