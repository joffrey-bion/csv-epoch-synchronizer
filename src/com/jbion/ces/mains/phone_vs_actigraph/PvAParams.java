package com.jbion.ces.mains.phone_vs_actigraph;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.xml.sax.SAXException;

import com.jbion.ces.actigraph.ActigraphFileFormat;
import com.jbion.ces.config.Config;
import com.jbion.ces.config.Profile;
import com.jbion.ces.phone.PhoneLocation;
import com.jbion.ces.phone.PhoneRawToEpParams;
import com.jbion.ces.phone.PhoneType;
import com.jbion.ces.phone.decision.LabelAppender;
import com.jbion.ces.phone.decision.LabelAppenderParams;
import com.jbion.utils.csv.Csv;
import com.jbion.utils.paths.Paths;
import com.jbion.utils.stats.FlowStats;
import com.jbion.utils.xml.serialization.serializers.DateArraySerializer;
import com.jbion.utils.xml.serialization.serializers.DateSerializer;
import com.jbion.utils.xml.serialization.serializers.EnumSerializer;
import com.jbion.utils.xml.serialization.serializers.SimpleSerializer;
import com.jbion.utils.xml.serialization.parameters.Parameters;
import com.jbion.utils.xml.serialization.parameters.ParamsSchema;
import com.jbion.utils.xml.serialization.parameters.SpecificationNotMetException;

public class PvAParams extends Parameters implements PhoneRawToEpParams, LabelAppenderParams,
        ClassificationAnalysisParams {

    private static final int DEFAULT_EPOCH_WIDTH_SEC = 1;
    private static final ActigraphFileFormat DEFAULT_ACTIG_FILE_FORMAT = ActigraphFileFormat.EXPORTED;
    
    static final String INPUT_PHONE_FILE = "phone-raw-file";
    static final String INPUT_ACTIG_EPOCH_FILE = "actigraph-file";
    static final String INPUT_PARTICIPANT_FILE = "participant-file";
    static final String OUTPUT_VALIDATION_FILE = "validation-results-file";
    static final String OUTPUT_TRAINING_SET_FILE = "training-set-file";
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
    private static final EnumSerializer<ActigraphFileFormat> FORMAT_SER = new EnumSerializer<>(
            ActigraphFileFormat.class);
    private static final EnumSerializer<PhoneType> PHONE_TYPE_SER = new EnumSerializer<>(
            PhoneType.class);
    private static final EnumSerializer<PhoneLocation> PHONE_LOCATION_SER = new EnumSerializer<>(
            PhoneLocation.class);
    private static final EnumSerializer<Profile> PROFILE_SER = new EnumSerializer<>(Profile.class);

    private static final ParamsSchema SCHEMA = new ParamsSchema("parameters", 3);
    static {
        SCHEMA.addParam(OUTPUT_TRAINING_SET_FILE, SimpleSerializer.STRING,
                "Name of the output dataset file");
        SCHEMA.addParam(OUTPUT_VALIDATION_FILE, SimpleSerializer.STRING,
                "Name of the output validation results file");
        SCHEMA.addParam(INPUT_PHONE_FILE, SimpleSerializer.STRING, "The phone's raw samples file");
        SCHEMA.addParam(INPUT_ACTIG_EPOCH_FILE, SimpleSerializer.STRING, "The actigraph's epoch file");
        SCHEMA.addParam(INPUT_PARTICIPANT_FILE, SimpleSerializer.STRING, "The participant's info file");
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
    public String phoneRawFile;
    /** Path to the file containing the epochs from the actigraph. */
    public String actigraphEpFile;
    /** Path to the file containing the raw data from the phone. */
    public String participantFile;
    /** Path to the XML file containing the decision tree. */
    public String classifierFile;
    /** Path to the output training set file. */
    public String outputTrainingSetFile;
    /** Path to the output validation results file. */
    public String outputValidationFile;
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
    public PvAParams(String paramsFilePath) throws IOException, SAXException,
            SpecificationNotMetException {
        super(SCHEMA);
        loadFromXml(paramsFilePath);
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
        this.phoneRawFile = getString(INPUT_PHONE_FILE);
        this.participantFile = getString(INPUT_PARTICIPANT_FILE);
        this.actigraphEpFile = getString(INPUT_ACTIG_EPOCH_FILE);
        this.outputTrainingSetFile = getString(OUTPUT_TRAINING_SET_FILE);
        this.outputValidationFile = getString(OUTPUT_VALIDATION_FILE);
        this.startTime = get(START_TIME, TIMESTAMP_SER) * 1000000;
        this.stopTime = get(STOP_TIME, TIMESTAMP_SER) * 1000000;
        this.actigraphFileFormat = get(ACTIG_FILE_FORMAT, FORMAT_SER);
        this.profile = get(PROFILE, PROFILE_SER);
        this.phoneType = get(PHONE_TYPE, PHONE_TYPE_SER);
        this.phoneLocation = get(PHONE_LOCATION, PHONE_LOCATION_SER);
        this.classifierFile = Config.get().getClassifier(get(PROFILE, PROFILE_SER),
                get(PHONE_TYPE, PHONE_TYPE_SER));
        setWindowFields(getInteger(EPOCH_WIDTH_SEC));
        Long[] phoneSpikes = get(PHONE_SPIKES_LIST, SPIKES_SER);
        Long[] actigSpikes = get(ACTIG_SPIKES_LIST, SPIKES_SER);
        setDelay(phoneSpikes, actigSpikes);
    }

    @Override
    public void loadFromXml(String xmlFilePath) throws IOException, SAXException,
            SpecificationNotMetException {
        super.loadFromXml(xmlFilePath);
        resolve(INPUT_PHONE_FILE, xmlFilePath);
        resolve(INPUT_ACTIG_EPOCH_FILE, xmlFilePath);
        resolve(INPUT_PARTICIPANT_FILE, xmlFilePath);
        resolve(OUTPUT_TRAINING_SET_FILE, xmlFilePath);
        resolve(OUTPUT_VALIDATION_FILE, xmlFilePath);
    }

    @Override
    public void saveToXml(String xmlFilePath) throws IOException, SpecificationNotMetException {
        relativize(INPUT_PHONE_FILE, xmlFilePath);
        relativize(INPUT_ACTIG_EPOCH_FILE, xmlFilePath);
        relativize(INPUT_PARTICIPANT_FILE, xmlFilePath);
        relativize(OUTPUT_TRAINING_SET_FILE, xmlFilePath);
        relativize(OUTPUT_VALIDATION_FILE, xmlFilePath);
        super.saveToXml(xmlFilePath);
    }

    /**
     * Replace an absolute path parameter by a path relative to the specified
     * {@code basePath}.
     * 
     * @param key
     *            The key of the parameter to relativize.
     * @param basePath
     *            The base path to use.
     */
    private void relativize(String key, String basePath) {
        set(key, Paths.relativizeSibling(basePath, getString(key)));
    }

    /**
     * Replace a relative path parameter by an absolute path.
     * 
     * @param key
     *            The key of the parameter to relativize.
     * @param basePath
     *            The base path to use to resolve the relative path.
     */
    private void resolve(String key, String basePath) {
        set(key, Paths.resolveSibling(basePath, getString(key)));
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
        return phoneRawFile;
    }

    @Override
    public String getPhoneEpochFilePath() {
        return Csv.removeExtension(phoneRawFile) + "-temp.csv";
    }

    @Override
    public String getClassifierFilePath() {
        return classifierFile;
    }

    @Override
    public String getUnlabeledDatasetFilePath() {
        return getPhoneEpochFilePath();
    }

    @Override
    public String getLabeledDatasetFilePath() {
        return Csv.removeExtension(phoneRawFile) + "-temp-labeled.csv";
    }

    @Override
    public String getTwoLabeledFile() {
        return Csv.removeExtension(phoneRawFile) + "-temp-double-labeled.csv";
    }

    @Override
    public String getHeaderClassifiedAs() {
        return LabelAppender.APPENDED_HEADER;
    }

    @Override
    public String getHeaderTruth() {
        return PvAMerger.APPENDED_HEADER;
    }

    @Override
    public List<String> getLevels() {
        return Arrays.asList(Config.get().cutPointsSet.getLevels());
    }

    @Override
    public boolean shouldRemoveTimestamps() {
        return false;
    }
}
