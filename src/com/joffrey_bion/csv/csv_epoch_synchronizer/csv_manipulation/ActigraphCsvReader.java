package com.joffrey_bion.csv.csv_epoch_synchronizer.csv_manipulation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import com.joffrey_bion.csv.TimestampedCsvReader;

/**
 * A reader for the CSV file produced by ActiLife when exporting the actigraph's
 * epochs. All the constants relative to the organization of the CSV file are
 * hardcoded in this class.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class ActigraphCsvReader extends TimestampedCsvReader {

    private static final int NB_HEADER_LINES = 2;
    
    private static final int VM_COL = 5;
    private static final int DATE_COL = 0;
    private static final int TIME_COL = 1;
    private static final String DATE_FORMAT = "M/d/yyyy";
    private static final String TIME_FORMAT = "hh:mm:ss a";

    public ActigraphCsvReader(String filename) throws FileNotFoundException, NotACsvFileException {
        super(filename);
    }

    @Override
    public long extractTimestamp(String[] line) throws IOException {
        String timestamp = line[DATE_COL] + " " + line[TIME_COL];
        String format = DATE_FORMAT + " " + TIME_FORMAT;
        try {
            return timestampStrToNanos(timestamp, format);
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }

    public static double extractCountsPerMinutes(String[] line, long epochWidthNanos) {
        long nbEpochsPerMin = ((long) 60 * 1000 * 1000000) / epochWidthNanos;
        return Double.valueOf(line[VM_COL]) * nbEpochsPerMin;
    }

    public void skipHeaders() throws IOException {
        readRows(NB_HEADER_LINES);
    }
}
