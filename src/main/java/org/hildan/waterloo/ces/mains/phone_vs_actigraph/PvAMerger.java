package org.hildan.waterloo.ces.mains.phone_vs_actigraph;

import java.io.IOException;
import java.util.Arrays;

import org.hildan.utils.csv.CsvWriter;
import org.hildan.utils.csv.NotACsvFileException;
import org.hildan.utils.dates.DateHelper;
import org.hildan.waterloo.ces.actigraph.ActigraphCsvReader;
import org.hildan.waterloo.ces.actigraph.ActigraphFileFormat;
import org.hildan.waterloo.ces.actigraph.CutPointsSet;
import org.hildan.waterloo.ces.config.Config;
import org.hildan.waterloo.ces.phone.PhoneCsvReader;

public class PvAMerger {

    public static final String APPENDED_HEADER = "ActigraphLevel";

    private final PhoneCsvReader phone;

    private final ActigraphCsvReader actigraph;

    private final CsvWriter writer;

    public PvAMerger(String phoneEpFilename, String actigraphEpFilename, String destFilename,
            ActigraphFileFormat actigraphFileFormat) throws IOException, NotACsvFileException {
        phone = new PhoneCsvReader(phoneEpFilename);
        actigraph = new ActigraphCsvReader(actigraphEpFilename, actigraphFileFormat);
        writer = new CsvWriter(destFilename);
    }

    public void createLabeledFile(PvAParams props) throws IOException {
        // write new columns headers
        final String[] headers = shiftLeftAndAppend(phone.readRow(), APPENDED_HEADER);
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
        final CutPointsSet labeler = Config.get().cutPointsSet;
        while ((linePhone = phone.readRow()) != null && (lineActigraph = actigraph.readRow()) != null) {
            timestampPhone = phone.extractTimestamp(linePhone);
            timestampActigraph = actigraph.extractTimestamp(lineActigraph);
            if (timestampPhone != timestampActigraph) {
                System.err.println("Error:");
                System.err.println("   phone time: " + DateHelper.displayTimestamp(timestampPhone));
                System.err.println("   actigraph time: " + DateHelper.displayTimestamp(timestampActigraph));
                throw new RuntimeException("phone and actigraph timestamps do not correspond");
            } else if (timestampPhone > props.stopTime) {
                System.err.println("Internal error:");
                System.err.println("   phone stop time: " + DateHelper.displayTimestamp(props.stopTime));
                System.err.println("   phone epoch time: " + DateHelper.displayTimestamp(timestampPhone));
                throw new RuntimeException("epoch beyond stoptime in intermediate file");
            }
            final double cpm = actigraph.extractCountsPerMinutes(lineActigraph, props.getEpochWidthNano());
            final String[] row = shiftLeftAndAppend(linePhone, labeler.countsToLevel(cpm));
            writer.writeRow(row);
        }
        phone.close();
        actigraph.close();
        writer.close();
    }

    private static String[] shiftLeftAndAppend(String[] original, String addedCol) {
        final String[] res = Arrays.copyOfRange(original, 1, original.length + 1);
        res[res.length - 1] = addedCol;
        return res;
    }
}
