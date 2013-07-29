package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_actigraph;

import java.text.ParseException;

import com.joffrey_bion.csv_epoch_synchronizer.actigraph.ActigraphFileFormat;
import com.joffrey_bion.csv_epoch_synchronizer.config.Config;
import com.joffrey_bion.csv_epoch_synchronizer.phone.PhoneRawToEpParams;
import com.joffrey_bion.utils.dates.DateHelper;
import com.joffrey_bion.utils.stats.FlowStats;

public class PvAParams implements PhoneRawToEpParams {

    private static final String SPIKE_TIMESTAMP_FORMAT = "HH:mm:ss.SSS";
    private static final String START_STOP_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final boolean USE_DEFAULT_WIDTH = true;
    private static final int DEFAULT_EPOCH_WIDTH_SEC = 1;

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
    /** Actigraph epochs file format */
    public ActigraphFileFormat actigraphFileFormat;
    /** Window width in nanoseconds */
    private long windowWidthNano;
    /** Epochs width in nanoseconds */
    private long epochWidthNano;
    /** Time between the beginning of the window and the beginning of the epoch */
    private long winBeginToEpBegin;
    /**
     * Delay in nanoseconds to add to a phone time to find the corresponding
     * actigraph time.
     */
    private long delay;

    @SuppressWarnings("serial")
    public class ArgumentFormatException extends Exception {
        public ArgumentFormatException(String message) {
            super(message);
        }
    }

    /**
     * Creates a {@code PvAParams} object based on the given raw parameters.
     * 
     * @param rawParams
     *            A set of raw parameters to parse.
     * @throws ArgumentFormatException
     *             If one of the parameters is not in the expected format.
     */
    public PvAParams(PvARawParams rawParams) throws ArgumentFormatException {
        if (rawParams.phoneRawFile == null || rawParams.phoneRawFile.equals("")) {
            throw new ArgumentFormatException("Phone raw data file must be specified.");
        } else {
            phoneRawFilename = rawParams.phoneRawFile;
        }
        if (rawParams.actigEpFile == null || rawParams.actigEpFile.equals("")) {
            throw new ArgumentFormatException("Actigraph epoch file must be specified.");
        } else {
            actigraphEpFilename = rawParams.actigEpFile;
        }
        if (rawParams.outputFile != null && !rawParams.outputFile.equals("")) {
            outputFilename = rawParams.outputFile;
        }
        try {
            startTime = DateHelper.timestampStrToNanos(rawParams.startTime,
                    PvAParams.START_STOP_TIMESTAMP_FORMAT);
        } catch (ParseException e) {
            throw new ArgumentFormatException("Incorrect format for start time.");
        }
        try {
            stopTime = DateHelper.timestampStrToNanos(rawParams.stopTime,
                    PvAParams.START_STOP_TIMESTAMP_FORMAT);
        } catch (ParseException e) {
            throw new ArgumentFormatException("Incorrect format for stop time.");
        }
        if (startTime > stopTime) {
            throw new IllegalArgumentException("Start time must be less than stop time.");
        }
        if (rawParams.actigraphFileFormat == null) {
            actigraphFileFormat = ActigraphFileFormat.EXPORTED;
        } else {
            actigraphFileFormat = ActigraphFileFormat.valueOf(rawParams.actigraphFileFormat);
        }
        if (USE_DEFAULT_WIDTH) {
            setWindowFields(DEFAULT_EPOCH_WIDTH_SEC);
        } else {
            try {
                int epochWidth = Integer.parseInt(rawParams.epochWidthSec);
                if (epochWidth > Config.get().windowWidthSec) {
                    throw new ArgumentFormatException("The epoch cannot be longer than the window.");
                }
                setWindowFields(epochWidth);
            } catch (NumberFormatException e) {
                throw new ArgumentFormatException(
                        "Incorrect format for epoch or window width, integer expected.");
            }
        }
        try {
            if (rawParams.phoneSpikes.length != rawParams.actigraphSpikes.length) {
                throw new ArgumentFormatException("Incorrect format for spikes time.");
            }
            setDelay(rawParams.phoneSpikes, rawParams.actigraphSpikes);
        } catch (ParseException e) {
            throw new ArgumentFormatException("Incorrect format for spikes time.");
        }
    }

    /**
     * Sets the fields related to the time window and the labeled part.
     * 
     * @param epochWidthSec
     *            The width of the labeled part of the time window in seconds.
     */
    private void setWindowFields(int epochWidthSec) {
        this.windowWidthNano = Config.get().windowWidthSec * 1000 * 1000000;
        this.epochWidthNano = epochWidthSec * 1000 * 1000000;
        this.winBeginToEpBegin = (windowWidthNano - epochWidthNano) / 2;
    }

    /**
     * Calculate the delay between the phone's time and the actigraph's time
     * according to the given spikes timestamps in both references. These spikes were
     * obtained by shaking both devices together.
     * 
     * @param phoneSpikes
     *            Timestamps of the spikes in the phone's time reference.
     * @param actigraphSpikes
     *            Timestamps of the same spikes in the actigraph's reference.
     * @throws ParseException
     *             If a startTime does not match the expected format
     *             {@link #SPIKE_TIMESTAMP_FORMAT}.
     */
    private void setDelay(String[] phoneSpikes, String[] actigraphSpikes) throws ParseException {
        FlowStats delayStats = new FlowStats();
        long phoneTime;
        long actigraphTime;
        for (int i = 0; i < phoneSpikes.length / 2; i++) {
            phoneTime = DateHelper.timestampStrToNanos(phoneSpikes[i], SPIKE_TIMESTAMP_FORMAT);
            actigraphTime = DateHelper.timestampStrToNanos(actigraphSpikes[i],
                    SPIKE_TIMESTAMP_FORMAT);
            delayStats.add(actigraphTime - phoneTime);
        }
        delay = Double.valueOf(delayStats.mean()).longValue();
    }

    /**
     * Returns the delay in nanoseconds to add to a phone time to find the
     * corresponding actigraph time.
     * 
     * @return the delay between the phone time and actigraph time.
     */
    @Override
    public long getDelay() {
        return delay;
    }

    /**
     * Returns the width of the labeled part of the time window.
     * 
     * @return The width of the labeled part of the time window.
     */
    @Override
    public long getEpochWidthNano() {
        return epochWidthNano;
    }

    /**
     * Returns the width of the time window.
     * 
     * @return The width of the time window.
     */
    @Override
    public long getWindowWidthNano() {
        return windowWidthNano;
    }

    /**
     * Returns the time between the beginning of the time window and the beginning of
     * the labeled part.
     * 
     * @return The time between the beginning of the time window and the beginning of
     *         the labeled part.
     */
    @Override
    public long getWinBeginToEpBegin() {
        return winBeginToEpBegin;
    }

    @Override
    public long getPhoneStartTime() {
        return startTime - delay;
    }

    @Override
    public long getPhoneStopTime() {
        return stopTime - delay;
    }
}
