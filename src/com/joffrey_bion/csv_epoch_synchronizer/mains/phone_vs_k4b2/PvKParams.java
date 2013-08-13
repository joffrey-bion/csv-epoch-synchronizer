package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.joffrey_bion.utils.stats.FlowStats;
import com.joffrey_bion.utils.xml.serializers.DateArraySerializer;
import com.joffrey_bion.utils.xml.serializers.DurationArraySerializer;
import com.joffrey_bion.utils.xml.serializers.SimpleSerializer;
import com.joffrey_bion.xml_parameters_serializer.Parameters;
import com.joffrey_bion.xml_parameters_serializer.ParamsSchema;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

public class PvKParams {
    
    static final long PHONE_EPOCH_WIDTH_SEC = 2;

    private static final String KEY_PHONE_FILE = "phone-raw-file";
    private static final String KEY_K4B2_FILE = "k4b2-file";
    private static final String KEY_TREE_FILE = "decision-tree";
    private static final String KEY_NB_SYNC_MARKERS = "nb-sync-markers";
    private static final String KEY_PHONE_SPIKES = "phone-spikes";
    private static final String KEY_K4B2_SPIKES = "k4b2-spikes";

    private static final String PHONE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String K4B2_FORMAT = "HH:mm:ss";
    private static final DateArraySerializer PHONE_SER = new DateArraySerializer(PHONE_FORMAT);
    private static final DurationArraySerializer K4B2_SER = new DurationArraySerializer(K4B2_FORMAT);

    private static final ParamsSchema SCHEMA = new ParamsSchema(1);
    static {
        SCHEMA.addParam(KEY_PHONE_FILE, SimpleSerializer.STRING);
        SCHEMA.addParam(KEY_K4B2_FILE, SimpleSerializer.STRING);
        SCHEMA.addParam(KEY_TREE_FILE, SimpleSerializer.STRING);
        SCHEMA.addParam(KEY_NB_SYNC_MARKERS, SimpleSerializer.INTEGER);
        SCHEMA.addParam(KEY_PHONE_SPIKES, PHONE_SER, "Format: " + PHONE_FORMAT);
        SCHEMA.addParam(KEY_K4B2_SPIKES, K4B2_SER, "Format: " + K4B2_FORMAT);
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
    public String xmlTreeFile;
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
     * Creates a new {@code PvKParams} object from the specified XML file.
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
    public PvKParams(String paramsFilePath) throws IOException, SAXException,
            SpecificationNotMetException {
        Parameters params = Parameters.loadFromXml(paramsFilePath, SCHEMA);
        this.phoneRawFile = params.getString(KEY_PHONE_FILE);
        this.k4b2File = params.getString(KEY_K4B2_FILE);
        this.xmlTreeFile = params.getString(KEY_TREE_FILE);
        this.nbSyncMarkers = params.getInteger(KEY_NB_SYNC_MARKERS);
        Long[] phoneSpikes = params.get(KEY_PHONE_SPIKES, PHONE_SER);
        Long[] k4b2Spikes = params.get(KEY_K4B2_SPIKES, K4B2_SER);
        setDelay(phoneSpikes, k4b2Spikes);
    }

    /**
     * Calculate the delay between the phone's time and the K4b2's time according to
     * the given spikes timestamps in both references.
     * 
     * @param phoneSpikes
     *            Timestamps of the spikes in the phone's time reference in
     *            milliseconds.
     * @param actigraphSpikes
     *            Timestamps of the same spikes in the actigraph's reference in
     *            milliseconds.
     */
    private void setDelay(Long[] phoneSpikes, Long[] k4b2Spikes) {
        FlowStats delayStats = new FlowStats();
        for (int i = 0; i < phoneSpikes.length / 2; i++) {
            delayStats.add(k4b2Spikes[i] - phoneSpikes[i]);
        }
        delayPhoneToK4b2 = Double.valueOf(delayStats.mean()).longValue();
    }

}
