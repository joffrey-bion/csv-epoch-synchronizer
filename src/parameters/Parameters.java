package parameters;

import java.text.ParseException;


import com.joffrey_bion.csv.Csv;
import com.joffrey_bion.csv.csv_epoch_synchronizer.row_statistics.FlowStats;

public class Parameters {

    public static final String SPIKE_TIMESTAMP_FORMAT = "HH:mm:ss.SSS";
    public static final String START_STOP_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private static final boolean USE_DEFAULTS_WIDTHS = true;
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
    private long delay;

    public boolean deleteIntermediateFile = true;

    @SuppressWarnings("serial")
    public class ArgumentFormatException extends Exception {
        public ArgumentFormatException(String message) {
            super(message);
        }
    }

    public Parameters(RawParameters raw) throws ArgumentFormatException {
        if (raw.phoneRawFile == null || raw.phoneRawFile.equals("")) {
            throw new ArgumentFormatException("Phone raw data file must be specified.");
        } else {
            phoneRawFilename = raw.phoneRawFile;
        }
        if (raw.actigEpFile == null || raw.actigEpFile.equals("")) {
            throw new ArgumentFormatException("Actigraph epoch file must be specified.");
        } else {
            actigraphEpFilename = raw.actigEpFile;
        }
        if (raw.outputFile != null && !raw.outputFile.equals("")) {
            outputFilename = raw.outputFile;
        }
        try {
            startTime = Csv.timestampStrToNanos(raw.startTime,
                    Parameters.START_STOP_TIMESTAMP_FORMAT);
        } catch (ParseException e) {
            throw new ArgumentFormatException("Incorrect format for start timestamp.");
        }
        try {
            stopTime = Csv
                    .timestampStrToNanos(raw.stopTime, Parameters.START_STOP_TIMESTAMP_FORMAT);
        } catch (ParseException e) {
            throw new ArgumentFormatException("Incorrect format for stop timestamp.");
        }
        if (USE_DEFAULTS_WIDTHS) {
            windowWidthSec = DEFAULT_WINDOW_WIDTH_SEC;
            epochWidthSec = DEFAULT_EPOCH_WIDTH_SEC;
            updateWindowFields();
        } else {
            try {
                int epochWidth = Integer.parseInt(raw.epochWidthSec);
                int windowWidth = Integer.parseInt(raw.windowWidthSec);
                if (epochWidth > windowWidth) {
                    throw new ArgumentFormatException("The epoch cannot be longer than the window.");
                }
                setEpochWidth(epochWidth);
                setWindowWidth(windowWidth);
            } catch (NumberFormatException e) {
                throw new ArgumentFormatException(
                        "Incorrect format for epoch or window width, integer expected.");
            }
        }
        deleteIntermediateFile = raw.deleteIntermediateFile;
        try {
            if (raw.phoneSpikes.length != raw.actigraphSpikes.length) {
                throw new ArgumentFormatException("Incorrect format for spikes timestamp.");
            }
            setDelay(raw.phoneSpikes, raw.actigraphSpikes);
        } catch (ParseException e) {
            throw new ArgumentFormatException("Incorrect format for spikes timestamp.");
        }
    }

    public void setDelay(String[] phoneSpikes, String[] actigraphSpikes) throws ParseException {
        FlowStats delayStats = new FlowStats();
        long phoneTime;
        long actigraphTime;
        for (int i = 0; i < phoneSpikes.length / 2; i++) {
            phoneTime = Csv.timestampStrToNanos(phoneSpikes[i], SPIKE_TIMESTAMP_FORMAT);
            actigraphTime = Csv.timestampStrToNanos(actigraphSpikes[i], SPIKE_TIMESTAMP_FORMAT);
            delayStats.add(actigraphTime - phoneTime);
        }
        delay = Double.valueOf(delayStats.mean()).longValue();
    }

    public long getDelay() {
        return delay;
    }

    public void setWindowWidth(int windowWidthSec) {
        this.windowWidthSec = windowWidthSec;
        updateWindowFields();
    }

    public void setEpochWidth(int epochWidthSec) {
        this.epochWidthSec = epochWidthSec;
        updateWindowFields();
    }

    private void updateWindowFields() {
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
