package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_actigraph;

import java.io.IOException;
import java.text.ParseException;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv_epoch_synchronizer.actigraph.ActigraphFileFormat;
import com.joffrey_bion.csv_epoch_synchronizer.config.Config;
import com.joffrey_bion.csv_epoch_synchronizer.phone.PhoneRawToEpParams;
import com.joffrey_bion.utils.stats.FlowStats;
import com.joffrey_bion.utils.xml.serializers.DateArraySerializer;
import com.joffrey_bion.utils.xml.serializers.DateSerializer;
import com.joffrey_bion.utils.xml.serializers.SimpleSerializer;
import com.joffrey_bion.xml_parameters_serializer.Parameters;
import com.joffrey_bion.xml_parameters_serializer.ParamsSchema;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

public class PvAParams extends Parameters implements PhoneRawToEpParams {

    private static final int DEFAULT_EPOCH_WIDTH_SEC = 1;
    private static final ActigraphFileFormat DEFAULT_ACTIG_FILE_FORMAT = ActigraphFileFormat.EXPORTED;
    private static final String DEFAULT_OUTPUT_FILE_PATH = "dataset.csv";

    public static final String PHONE_FILE_PATH = "phone-raw-file";
    public static final String OUTPUT_FILE_PATH = "output-file";
    public static final String ACTIG_FILE_PATH = "actigraph-file";
    public static final String ACTIG_FILE_FORMAT = "actigraph--file-format";
    public static final String START_TIME = "start-time";
    public static final String STOP_TIME = "stop-time";
    public static final String EPOCH_WIDTH_SEC = "epoch-width";
    public static final String PHONE_SPIKES_LIST = "phone-spikes";
    public static final String ACTIG_SPIKES_LIST = "actig-spikes";

    static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final DateSerializer TIMESTAMP_SER = new DateSerializer(TIMESTAMP_FORMAT);
    private static final DateArraySerializer SPIKES_SER = new DateArraySerializer(TIMESTAMP_FORMAT);
    private static final SimpleSerializer<ActigraphFileFormat> FORMAT_SER = new SimpleSerializer<ActigraphFileFormat>(
            ActigraphFileFormat.class) {
        @Override
        public ActigraphFileFormat deserialize(String s) throws ParseException {
            return ActigraphFileFormat.valueOf(s);
        }
    };

    private static final ParamsSchema SCHEMA = new ParamsSchema("parameters", 3);
    static {
        SCHEMA.addParam(OUTPUT_FILE_PATH, SimpleSerializer.STRING, false, DEFAULT_OUTPUT_FILE_PATH,
                "Name of the output dataset file");
        SCHEMA.addParam(PHONE_FILE_PATH, SimpleSerializer.STRING, "The phone's raw samples file");
        SCHEMA.addParam(ACTIG_FILE_PATH, SimpleSerializer.STRING, "The actigraph's epoch file");
        SCHEMA.addParam(ACTIG_FILE_FORMAT, FORMAT_SER, false, DEFAULT_ACTIG_FILE_FORMAT,
                "EXPORTED (via \"export all epochs\") or CONVERTED (via File > Import/Export/Convert)");
        SCHEMA.addParam(EPOCH_WIDTH_SEC, SimpleSerializer.INTEGER, false, DEFAULT_EPOCH_WIDTH_SEC,
                "The epoch width of the actigraph file in seconds");
        SCHEMA.addParam(START_TIME, TIMESTAMP_SER, "Start time in actigraph reference ("
                + TIMESTAMP_FORMAT + ")");
        SCHEMA.addParam(STOP_TIME, TIMESTAMP_SER, "Stop time in actigraph reference ("
                + TIMESTAMP_FORMAT + ")");
        SCHEMA.addParam(PHONE_SPIKES_LIST, SPIKES_SER,
                "Phone acceleration spikes, in the right order (" + TIMESTAMP_FORMAT + ")");
        SCHEMA.addParam(ACTIG_SPIKES_LIST, SPIKES_SER,
                "Actigraph acceleration spikes, in the right order (" + TIMESTAMP_FORMAT + ")");
    }

    /** Path to the file containing the raw data from the phone. */
    public String phoneRawFilename;
    /** Path to the file containing the epochs from the actigraph. */
    public String actigraphEpFilename;
    /** Path to the output file. */
    public String outputFilename;
    /** Start time in actigraph reference in nanoseconds. */
    public long startTime;
    /** Stop time in actigraph reference in nanoseconds. */
    public long stopTime;
    /** Actigraph epochs file format. */
    public ActigraphFileFormat actigraphFileFormat;
    /** Window width in nanoseconds. */
    private long windowWidthNano;
    /** Epochs width in nanoseconds. */
    private long epochWidthNano;
    /** Time between the beginning of the window and the beginning of the epoch. */
    private long winBeginToEpBegin;
    /**
     * Delay in nanoseconds to add to a phone time to find the corresponding
     * actigraph time.
     */
    private long delay;

    /**
     * Creates a new {@link PvAParams} object from the specified XML file.
     * 
     * @param paramsFilePath
     *            The path to the XML file to read the parameters from.
     * @throws IOException
     *             If an error occurs while reading the file.
     * @throws SAXException
     *             If any XML parse error occurs.
     * @throws SpecificationNotMetException
     *             If the XML file does not meet the schema's requirements.
     */
    public PvAParams(String xmlFilePath) throws IOException, SAXException,
            SpecificationNotMetException {
        super(SCHEMA);
        loadFromXml(xmlFilePath);
        populatePublicFields();
    }

    /**
     * Creates a new empty {@link PvAParams} object that has to be filled via
     * {@link Parameters}' methods. When all parameters are set, the method
     * {@link #populatePublicFields()} has to be called to make the public fields of
     * this class usable.
     */
    public PvAParams() {
        super(SCHEMA);
    }

    /**
     * Pull the data from this {@link Parameters} object and make it available more
     * efficiently through the public fields and getters.
     */
    public void populatePublicFields() {
        this.phoneRawFilename = getString(PHONE_FILE_PATH);
        this.actigraphEpFilename = getString(ACTIG_FILE_PATH);
        this.outputFilename = getString(OUTPUT_FILE_PATH);
        this.startTime = get(START_TIME, TIMESTAMP_SER);
        this.stopTime = get(STOP_TIME, TIMESTAMP_SER);
        this.actigraphFileFormat = get(ACTIG_FILE_FORMAT, FORMAT_SER);
        setWindowFields(getInteger(EPOCH_WIDTH_SEC));
        Long[] phoneSpikes = get(PHONE_SPIKES_LIST, SPIKES_SER);
        Long[] actigSpikes = get(ACTIG_SPIKES_LIST, SPIKES_SER);
        setDelay(phoneSpikes, actigSpikes);
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
     */
    private void setDelay(Long[] phoneSpikes, Long[] actigraphSpikes) {
        FlowStats delayStats = new FlowStats();
        long phoneTime;
        long actigraphTime;
        for (int i = 0; i < phoneSpikes.length / 2; i++) {
            phoneTime = phoneSpikes[i];
            actigraphTime = actigraphSpikes[i];
            delayStats.add(actigraphTime - phoneTime);
        }
        delay = Double.valueOf(delayStats.mean()).longValue();
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

    @Override
    public long getDelay() {
        return delay;
    }

    @Override
    public long getEpochWidthNano() {
        return epochWidthNano;
    }

    @Override
    public long getWindowWidthNano() {
        return windowWidthNano;
    }

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
