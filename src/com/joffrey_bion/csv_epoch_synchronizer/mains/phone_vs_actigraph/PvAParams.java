package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_actigraph;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv.Csv;
import com.joffrey_bion.csv_epoch_synchronizer.actigraph.ActigraphFileFormat;
import com.joffrey_bion.csv_epoch_synchronizer.config.Config;
import com.joffrey_bion.csv_epoch_synchronizer.config.Profile;
import com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2.PhoneLocation;
import com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2.PhoneType;
import com.joffrey_bion.csv_epoch_synchronizer.phone.PhoneRawToEpParams;
import com.joffrey_bion.utils.stats.FlowStats;
import com.joffrey_bion.utils.xml.serializers.DateArraySerializer;
import com.joffrey_bion.utils.xml.serializers.DateSerializer;
import com.joffrey_bion.utils.xml.serializers.EnumSerializer;
import com.joffrey_bion.utils.xml.serializers.SimpleSerializer;
import com.joffrey_bion.xml_parameters_serializer.Parameters;
import com.joffrey_bion.xml_parameters_serializer.ParamsSchema;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

public class PvAParams extends Parameters implements PhoneRawToEpParams {

    private static final int DEFAULT_EPOCH_WIDTH_SEC = 1;
    private static final ActigraphFileFormat DEFAULT_ACTIG_FILE_FORMAT = ActigraphFileFormat.EXPORTED;
    private static final String DEFAULT_OUTPUT_FILE_PATH = "dataset.csv";

    static final String PHONE_FILE_PATH = "phone-raw-file";
    static final String OUTPUT_FILE_PATH = "output-file";
    static final String ACTIG_FILE_PATH = "actigraph-file";
    static final String ACTIG_FILE_FORMAT = "actigraph-file-format";
    static final String START_TIME = "start-time";
    static final String STOP_TIME = "stop-time";
    static final String EPOCH_WIDTH_SEC = "epoch-width";
    static final String PHONE_SPIKES_LIST = "phone-spikes";
    static final String ACTIG_SPIKES_LIST = "actig-spikes";
    static final String PROFILE = "profile";
    static final String PHONE_TYPE = "phone-type";
    static final String PHONE_LOCATION = "phone-location";

    static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final DateSerializer TIMESTAMP_SER = new DateSerializer(TIMESTAMP_FORMAT);
    private static final DateArraySerializer SPIKES_SER = new DateArraySerializer(TIMESTAMP_FORMAT);
    private static final EnumSerializer<ActigraphFileFormat> FORMAT_SER = new EnumSerializer<>(ActigraphFileFormat.class);
    private static final EnumSerializer<PhoneType> PHONE_TYPE_SER = new EnumSerializer<>(PhoneType.class);
    private static final EnumSerializer<PhoneLocation> PHONE_LOCATION_SER = new EnumSerializer<>(PhoneLocation.class);
    private static final EnumSerializer<Profile> PROFILE_SER = new EnumSerializer<>(Profile.class);
    
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
        SCHEMA.addParam(PROFILE, PROFILE_SER, false, Profile.POCKET);
        SCHEMA.addParam(PHONE_LOCATION, PHONE_LOCATION_SER, false, PhoneLocation.LEFT);
        SCHEMA.addParam(PHONE_TYPE, PHONE_TYPE_SER, false, PhoneType.GYRO);
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
    /**
     * Delay in nanoseconds to add to a phone time to find the corresponding
     * actigraph time.
     */
    private long delay;
    /**
     * Holster or Pocket profile.
     */
    public Profile profile;
    /**
     * Whether the phone has a gyroscope or not.
     */
    public PhoneType phoneType;
    /**
     * Whether the phone was worn on the left or right side.
     */
    public PhoneLocation phoneLocation;
    
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
        this.startTime = get(START_TIME, TIMESTAMP_SER) * 1000000;
        this.stopTime = get(STOP_TIME, TIMESTAMP_SER) * 1000000;
        this.actigraphFileFormat = get(ACTIG_FILE_FORMAT, FORMAT_SER);
        this.profile = get(PROFILE, PROFILE_SER);
        this.phoneType = get(PHONE_TYPE, PHONE_TYPE_SER);
        this.phoneLocation = get(PHONE_LOCATION, PHONE_LOCATION_SER);
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
     *            Timestamps of the spikes in the phone's time reference, in
     *            milliseconds.
     * @param actigraphSpikes
     *            Timestamps of the same spikes in the actigraph's reference, in
     *            milliseconds.
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
        delay = Double.valueOf(delayStats.mean()).longValue() * 1000000;
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
    }

    @Override
    public long getDelayNano() {
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
        return (windowWidthNano - epochWidthNano) / 2;
    }

    @Override
    public long getPhoneStartTimeNano() {
        return startTime - delay;
    }

    @Override
    public long getPhoneStopTimeNano() {
        return stopTime - delay;
    }

    @Override
    public String getInputFilePath() {
        return phoneRawFilename;
    }

    @Override
    public String getPhoneEpochFilePath() {
        return Csv.removeExtension(phoneRawFilename) + "-temp.csv";
    }
}
