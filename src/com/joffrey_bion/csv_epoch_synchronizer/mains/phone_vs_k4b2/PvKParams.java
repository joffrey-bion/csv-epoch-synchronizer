package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv_epoch_synchronizer.phone.PhoneRawToEpParams;
import com.joffrey_bion.utils.stats.FlowStats;
import com.joffrey_bion.utlis.xml.serializers.DateArraySerializer;
import com.joffrey_bion.utlis.xml.serializers.DurationArraySerializer;
import com.joffrey_bion.utlis.xml.serializers.Serializer;
import com.joffrey_bion.xml_parameters_serializer.Parameters;
import com.joffrey_bion.xml_parameters_serializer.ParamsSchema;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

public class PvKParams implements PhoneRawToEpParams {
    
    private static final String KEY_PHONE_FILE = "phone-raw-file";
    private static final String KEY_K4B2_FILE = "phone-raw-file";
    private static final String KEY_NB_SYNC_MARKERS = "nb-sync-markers";
    private static final String KEY_PHONE_SPIKES = "phone-spikes";
    private static final String KEY_K4B2_SPIKES = "k4b2-spikes";

    private static final DateArraySerializer PHONE_SERIALIZER = new DateArraySerializer(
            "yyyy-MM-dd HH:mm:ss.SSS");
    private static final DurationArraySerializer K4B2_SERIALIZER = new DurationArraySerializer(
            "HH:mm:ss");

    private static final ParamsSchema SCHEMA = new ParamsSchema();
    static {
        SCHEMA.addParam(KEY_PHONE_FILE, Serializer.STRING);
        SCHEMA.addParam(KEY_K4B2_FILE, Serializer.STRING);
        SCHEMA.addParam(KEY_NB_SYNC_MARKERS, Serializer.INTEGER);
        SCHEMA.addParam(KEY_PHONE_SPIKES, PHONE_SERIALIZER);
        SCHEMA.addParam(KEY_K4B2_SPIKES, K4B2_SERIALIZER);
    }

    public String phoneRawFile;
    public String k4b2File;
    public Integer nbSyncMarkers;
    public long delayPhoneToK4b2;

    public PvKParams(String paramsFilePath) throws IOException, SAXException,
            SpecificationNotMetException {
        Parameters params = Parameters.loadFromXml(paramsFilePath, SCHEMA);
        phoneRawFile = params.getString(KEY_PHONE_FILE);
        k4b2File = params.getString(KEY_K4B2_FILE);
        nbSyncMarkers = params.getInteger(KEY_NB_SYNC_MARKERS);
        Long[] phoneSpikes = params.get(KEY_PHONE_SPIKES, PHONE_SERIALIZER);
        Long[] k4b2Spikes = params.get(KEY_K4B2_SPIKES, K4B2_SERIALIZER);
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

    /**
     * Returns the delay in milliseconds to add to a phone time to find the
     * corresponding K4b2 time.
     * 
     * @return the delay between the phone time and actigraph time.
     */
    @Override
    public long getDelay() {
        return delayPhoneToK4b2;
    }

    @Override
    public long getPhoneStartTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getPhoneStopTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getWindowWidthNano() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getEpochWidthNano() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getWinBeginToEpBegin() {
        // TODO Auto-generated method stub
        return 0;
    }
}
