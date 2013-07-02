package com.joffrey_bion.csv.csv_epoch_synchronizer;

import java.io.IOException;
import java.util.Arrays;

import com.joffrey_bion.csv.Csv.NotACsvFileException;
import com.joffrey_bion.csv.csv_epoch_synchronizer.csv_manipulation.ActigraphCsvReader;
import com.joffrey_bion.csv.csv_epoch_synchronizer.csv_manipulation.DateHelper;
import com.joffrey_bion.csv.csv_epoch_synchronizer.csv_manipulation.PhoneCsvReader;
import com.joffrey_bion.csv.csv_epoch_synchronizer.parameters.Parameters;
import com.joffrey_bion.csv.CsvWriter;

public class EpLabelsMerger {

    private PhoneCsvReader phone;
    private ActigraphCsvReader actigraph;
    private CsvWriter writer;

    public EpLabelsMerger(String phoneEpFilename, String actigraphEpFilename, String destFilename)
            throws IOException, NotACsvFileException {
        phone = new PhoneCsvReader(phoneEpFilename);
        actigraph = new ActigraphCsvReader(actigraphEpFilename);
        writer = new CsvWriter(destFilename);
    }

    public void createLabeledFile(Parameters props) throws IOException {
        // write new columns headers
        String[] headers = shiftLeftAndAppend(phone.readRow(), "Activity Level");
        writer.writeRow(headers);

        // reach start time in both files
        phone.skipToReachTimestamp(props.startTime);
        actigraph.skipHeaders();
        actigraph.skipToReachTimestamp(props.startTime);

        long timestampPhone;
        long timestampActigraph;
        String[] linePhone;
        String[] lineActigraph;
        CountsLabeler labeler = new CountsLabeler();
        while ((linePhone = phone.readRow()) != null
                && (lineActigraph = actigraph.readRow()) != null) {
            timestampPhone = phone.extractTimestamp(linePhone);
            timestampActigraph = actigraph.extractTimestamp(lineActigraph);
            if (timestampPhone != timestampActigraph) {
                DateHelper.displayTimestamp("phone timestamp", timestampPhone);
                DateHelper.displayTimestamp("actigraph timestamp", timestampActigraph);
                throw new RuntimeException("phone and actigraph timestamps do not correspond");
            } else if (timestampPhone > props.stopTime) {
                throw new RuntimeException(
                        "Internal error, epoch beyond stoptime in intermediate file");
            }
            double cpm = ActigraphCsvReader.extractCountsPerMinutes(lineActigraph,
                    props.getEpochWidthNano());
            String[] row = shiftLeftAndAppend(linePhone, labeler.countsToLabel(cpm));
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
