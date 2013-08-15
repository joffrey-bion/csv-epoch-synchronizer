package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv_epoch_synchronizer.config.Config;
import com.joffrey_bion.csv_epoch_synchronizer.config.Profile;
import com.joffrey_bion.utils.stats.FlowStats;
import com.joffrey_bion.utils.xml.serializers.DateArraySerializer;
import com.joffrey_bion.utils.xml.serializers.DurationArraySerializer;
import com.joffrey_bion.utils.xml.serializers.EnumSerializer;
import com.joffrey_bion.utils.xml.serializers.SimpleSerializer;
import com.joffrey_bion.xml_parameters_serializer.Parameters;
import com.joffrey_bion.xml_parameters_serializer.ParamsSchema;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

public class PvKParams extends Parameters {

    static final String PHONE_FILE_PATH = "phone-raw-file";
    static final String K4B2_FILE_PATH = "k4b2-file";
    static final String OUTPUT_FILE_PATH = "output-file";
    static final String WRITE_OUTPUT = "write-output";
    static final String PROFILE = "profile";
    static final String NB_SYNC_MARKERS = "nb-sync-markers";
    static final String PHONE_SPIKES_LIST = "phone-spikes";
    static final String K4B2_SPIKES_LIST = "k4b2-spikes";

    static final String PHONE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    static final String K4B2_FORMAT = "HH:mm:ss";
    
    private static final DateArraySerializer PHONE_SER = new DateArraySerializer(PHONE_FORMAT);
    private static final DurationArraySerializer K4B2_SER = new DurationArraySerializer(K4B2_FORMAT);
    private static final EnumSerializer<Profile> PROFILE_SER = new EnumSerializer<>(Profile.class);
    
    private static final ParamsSchema SCHEMA = new ParamsSchema(1);
    static {
        SCHEMA.addParam(PHONE_FILE_PATH, SimpleSerializer.STRING);
        SCHEMA.addParam(K4B2_FILE_PATH, SimpleSerializer.STRING);
        SCHEMA.addParam(OUTPUT_FILE_PATH, SimpleSerializer.STRING, false, "");
        SCHEMA.addParam(WRITE_OUTPUT, SimpleSerializer.BOOLEAN, false, false);
        SCHEMA.addParam(PROFILE, PROFILE_SER);
        SCHEMA.addParam(NB_SYNC_MARKERS, SimpleSerializer.INTEGER);
        SCHEMA.addParam(PHONE_SPIKES_LIST, PHONE_SER, "Format: " + PHONE_FORMAT);
        SCHEMA.addParam(K4B2_SPIKES_LIST, K4B2_SER, "Format: " + K4B2_FORMAT);
    }

    /**
     * Path to the CSV file containing the raw data from the phone.
     */
    public String phoneRawFile;
    /**
     * Path to the CSV file containing the epochs from the K4b2.
     */
    public String k4b2File;
    /**
     * Path to the XML file containing the decision tree.
     */
    public String classifierFile;
    /**
     * Path to the output file to write the results to.
     */
    public String outputFile;
    /**
     * Indicates whether the results should be written to a file.
     */
    public boolean writeOutput;
    /**
     * Number of markers used to synchronize the phone, which have to be skipped.
     */
    public Integer nbSyncMarkers;
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
        this.phoneRawFile = getString(PHONE_FILE_PATH);
        this.k4b2File = getString(K4B2_FILE_PATH);
        this.outputFile = getString(OUTPUT_FILE_PATH);
        this.writeOutput = getBoolean(WRITE_OUTPUT);
        this.classifierFile = Config.get().getClassifier(get(PROFILE, PROFILE_SER));
        this.nbSyncMarkers = getInteger(NB_SYNC_MARKERS);
        Long[] phoneSpikes = get(PHONE_SPIKES_LIST, PHONE_SER);
        Long[] k4b2Spikes = get(K4B2_SPIKES_LIST, K4B2_SER);
        FlowStats delayStats = new FlowStats();
        for (int i = 0; i < phoneSpikes.length / 2; i++) {
            delayStats.add(k4b2Spikes[i] - phoneSpikes[i]);
        }
        delayPhoneToK4b2 = Double.valueOf(delayStats.mean()).longValue();
    }
}
