package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv_epoch_synchronizer.config.Config;
import com.joffrey_bion.csv_epoch_synchronizer.config.Profile;
import com.joffrey_bion.csv_epoch_synchronizer.phone.PhoneLocation;
import com.joffrey_bion.csv_epoch_synchronizer.phone.PhoneType;
import com.joffrey_bion.utils.paths.Paths;
import com.joffrey_bion.utils.stats.FlowStats;
import com.joffrey_bion.utils.xml.serializers.DateArraySerializer;
import com.joffrey_bion.utils.xml.serializers.DurationArraySerializer;
import com.joffrey_bion.utils.xml.serializers.EnumSerializer;
import com.joffrey_bion.utils.xml.serializers.SimpleSerializer;
import com.joffrey_bion.xml_parameters_serializer.Parameters;
import com.joffrey_bion.xml_parameters_serializer.ParamsSchema;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

public class PvKParams extends Parameters {

    static final String PHONE_FILE = "phone-raw-file";
    static final String K4B2_FILE = "k4b2-file";
    static final String PARTICIPANT_FILE = "participant-file";
    static final String VALIDATION_FILE = "validation-results-file";
    static final String PROFILE = "profile";
    static final String PHONE_TYPE = "phone-type";
    static final String PHONE_LOCATION = "phone-location";
    static final String NB_SYNC_MARKERS = "nb-sync-markers";
    static final String PHONE_SPIKES_LIST = "phone-spikes";
    static final String K4B2_SPIKES_LIST = "k4b2-spikes";

    static final String PHONE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    static final String K4B2_FORMAT = "HH:mm:ss";

    private static final DateArraySerializer PHONE_SER = new DateArraySerializer(PHONE_FORMAT);
    private static final DurationArraySerializer K4B2_SER = new DurationArraySerializer(K4B2_FORMAT);
    private static final EnumSerializer<PhoneType> PHONE_TYPE_SER = new EnumSerializer<>(
            PhoneType.class);
    private static final EnumSerializer<PhoneLocation> PHONE_LOCATION_SER = new EnumSerializer<>(
            PhoneLocation.class);
    private static final EnumSerializer<Profile> PROFILE_SER = new EnumSerializer<>(Profile.class);

    private static final ParamsSchema SCHEMA = new ParamsSchema(3);
    static {
        SCHEMA.addParam(PHONE_FILE, SimpleSerializer.STRING);
        SCHEMA.addParam(K4B2_FILE, SimpleSerializer.STRING);
        SCHEMA.addParam(PARTICIPANT_FILE, SimpleSerializer.STRING);
        SCHEMA.addParam(VALIDATION_FILE, SimpleSerializer.STRING);
        SCHEMA.addParam(PROFILE, PROFILE_SER);
        SCHEMA.addParam(PHONE_LOCATION, PHONE_LOCATION_SER);
        SCHEMA.addParam(PHONE_TYPE, PHONE_TYPE_SER);
        SCHEMA.addParam(NB_SYNC_MARKERS, SimpleSerializer.INTEGER);
        SCHEMA.addParam(PHONE_SPIKES_LIST, PHONE_SER, "Format: " + PHONE_FORMAT);
        SCHEMA.addParam(K4B2_SPIKES_LIST, K4B2_SER, "Format: " + K4B2_FORMAT);
    }

    /** Path to the CSV file containing the raw data from the phone. */
    public String phoneRawFile;
    /** Path to the CSV file containing the epochs from the K4b2. */
    public String k4b2File;
    /** Path to the file containing the raw data from the phone. */
    public String participantFile;
    /** Path to the output validation results file. */
    public String outputValidationFile;
    /** Path to the XML file containing the decision tree. */
    public String classifierFile;
    /** Number of markers used to synchronize the phone, which have to be skipped. */
    public Integer nbSyncMarkers;
    /** Holster or Pocket profile. */
    public Profile profile;
    /** Whether the phone has a gyroscope or not. */
    public PhoneType phoneType;
    /** Whether the phone was worn on the left or right side. */
    public PhoneLocation phoneLocation;
    /**
     * Delay in nanoseconds to add to a phone time to find the corresponding
     * actigraph time.
     */
    public long delayPhoneToK4b2;

    /**
     * Creates a new {@link PvKParams} object from the specified XML file.
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
    public PvKParams(String xmlFilePath) throws IOException, SAXException,
            SpecificationNotMetException {
        super(SCHEMA);
        loadFromXml(xmlFilePath);
        populatePublicFields();
    }

    /**
     * Creates a new empty {@link PvKParams} object that has to be filled via
     * {@link Parameters}' methods. When all parameters are set, the method
     * {@link #populatePublicFields()} has to be called to make the public fields of
     * this class usable.
     */
    public PvKParams() {
        super(SCHEMA);
    }

    /**
     * Pull the data from this {@link Parameters} object and make it available more
     * efficiently through the public fields and getters.
     */
    public void populatePublicFields() {
        this.phoneRawFile = getString(PHONE_FILE);
        this.k4b2File = getString(K4B2_FILE);
        this.participantFile = getString(PARTICIPANT_FILE);
        this.outputValidationFile = getString(VALIDATION_FILE);
        this.classifierFile = Config.get().getClassifier(get(PROFILE, PROFILE_SER),
                get(PHONE_TYPE, PHONE_TYPE_SER));
        this.nbSyncMarkers = getInteger(NB_SYNC_MARKERS);
        this.profile = get(PROFILE, PROFILE_SER);
        this.phoneType = get(PHONE_TYPE, PHONE_TYPE_SER);
        this.phoneLocation = get(PHONE_LOCATION, PHONE_LOCATION_SER);
        Long[] phoneSpikes = get(PHONE_SPIKES_LIST, PHONE_SER);
        Long[] k4b2Spikes = get(K4B2_SPIKES_LIST, K4B2_SER);
        FlowStats delayStats = new FlowStats();
        for (int i = 0; i < phoneSpikes.length / 2; i++) {
            delayStats.add(k4b2Spikes[i] - phoneSpikes[i]);
        }
        delayPhoneToK4b2 = Double.valueOf(delayStats.mean()).longValue() * 1000000;
    }

    @Override
    public void loadFromXml(String xmlFilePath) throws IOException, SAXException,
            SpecificationNotMetException {
        super.loadFromXml(xmlFilePath);
        resolve(PHONE_FILE, xmlFilePath);
        resolve(K4B2_FILE, xmlFilePath);
        resolve(PARTICIPANT_FILE, xmlFilePath);
        resolve(VALIDATION_FILE, xmlFilePath);
    }

    @Override
    public void saveToXml(String xmlFilePath) throws IOException, SpecificationNotMetException {
        relativize(PHONE_FILE, xmlFilePath);
        relativize(K4B2_FILE, xmlFilePath);
        relativize(PARTICIPANT_FILE, xmlFilePath);
        relativize(VALIDATION_FILE, xmlFilePath);
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
}
