package com.joffrey_bion.csv.csv_epoch_synchronizer.parameters;

/**
 * A {@code RawParameters} object contains all the raw information read from an XML
 * parameter file. Therefore, most fields are just {@code String}s because they have
 * not been parsed yet.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class RawParameters {

    public static final int NB_MAX_SPIKES = 6;

    public String phoneRawFile;
    public String actigEpFile;
    public String outputFile;

    public String windowWidthSec;
    public String epochWidthSec;
    public String startTime;
    public String stopTime;
    public boolean deleteIntermediateFile;
    public String[] phoneSpikes;
    public String[] actigraphSpikes;
}
