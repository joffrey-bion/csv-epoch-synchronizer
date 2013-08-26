package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_actigraph;

import java.io.IOException;
import java.util.Arrays;

import com.joffrey_bion.csv.Csv.NotACsvFileException;
import com.joffrey_bion.csv_epoch_synchronizer.actigraph.ActigraphCsvReader;
import com.joffrey_bion.csv_epoch_synchronizer.actigraph.ActigraphFileFormat;
import com.joffrey_bion.csv_epoch_synchronizer.actigraph.CutPointsSet;
import com.joffrey_bion.csv_epoch_synchronizer.config.Config;
import com.joffrey_bion.csv_epoch_synchronizer.phone.PhoneCsvReader;
import com.joffrey_bion.csv.CsvWriter;
import com.joffrey_bion.utils.dates.DateHelper;

public class PvAMerger {

    public static final String APPENDED_HEADER = "ActigraphLevel";
    
    private PhoneCsvReader phone;
    private ActigraphCsvReader actigraph;
    private CsvWriter writer;

    /**
     * 
     * @param phoneEpFilename
     * @param actigraphEpFile
     * @param destFilename
     * @param actigraphFileFormat
     * @throws IOException
     * @throws NotACsvFileException
     */
    public PvAMerger(String phoneEpFilename, String actigraphEpFilename, String destFilename, ActigraphFileFormat actigraphFileFormat)
            throws IOException, NotACsvFileException {
        phone = new PhoneCsvReader(phoneEpFilename);
        actigraph = new ActigraphCsvReader(actigraphEpFilename, actigraphFileFormat);
        writer = new CsvWriter(destFilename);
    }

    public void createLabeledFile(PvAParams props) throws IOException {
        // write new columns headers
        String[] headers = shiftLeftAndAppend(phone.readRow(), APPENDED_HEADER);
        writer.writeRow(headers);

        // reach start time in both files
        // the phone times must already have been aligned (no delay correction here)
        phone.skipToReachTimestamp(props.startTime);
        actigraph.skipHeaders();
        actigraph.skipToReachTimestamp(props.startTime);

        long timestampPhone;
        long timestampActigraph;
        String[] linePhone;
        String[] lineActigraph;
        CutPointsSet labeler = Config.get().cutPointsSet;
        while ((linePhone = phone.readRow()) != null
                && (lineActigraph = actigraph.readRow()) != null) {
            timestampPhone = phone.extractTimestamp(linePhone);
            timestampActigraph = actigraph.extractTimestamp(lineActigraph);
            if (timestampPhone != timestampActigraph) {
                DateHelper.displayTimestamp("phone time", timestampPhone);
                DateHelper.displayTimestamp("actigraph time", timestampActigraph);
                throw new RuntimeException("phone and actigraph timestamps do not correspond");
            } else if (timestampPhone > props.stopTime) {
                throw new RuntimeException(
                        "Internal error, epoch beyond stoptime in intermediate file");
            }
            double cpm = actigraph
                    .extractCountsPerMinutes(lineActigraph, props.getEpochWidthNano());
            String[] row = shiftLeftAndAppend(linePhone, labeler.countsToLevel(cpm));
            writer.writeRow(row);
        }
        phone.close();
        actigraph.close();
        writer.close();
    }

    private static String[] shiftLeftAndAppend(String[] original, String addedCol) {
        String[] res = Arrays.copyOfRange(original, 1, original.length + 1);
        res[res.length - 1] = addedCol;
        return res;
    }
}
