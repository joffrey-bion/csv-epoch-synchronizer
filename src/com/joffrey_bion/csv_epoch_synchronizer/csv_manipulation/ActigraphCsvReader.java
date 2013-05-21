package com.joffrey_bion.csv_epoch_synchronizer.csv_manipulation;
import java.io.FileNotFoundException;
import java.io.IOException;


public class ActigraphCsvReader extends TimestampedCsvReader {

    private static final int VM_COL = 5;
    private static final int DATE_COL = 0;
    private static final int TIME_COL = 1;
    private static final String DATE_FORMAT = "M/d/yyyy";
    private static final String TIME_FORMAT = "hh:mm:ss a";

    public ActigraphCsvReader(String filename) throws FileNotFoundException {
        super(filename);
    }

    @Override
    public long extractTimestamp(String[] line) throws IOException {
        String timestamp = line[DATE_COL] + " " + line[TIME_COL];
        String format = DATE_FORMAT + " " + TIME_FORMAT;
        return timestampStrToNanos(timestamp, format);
    }
    
    public static double extractCountsPerMinutes(String[] line, long epochWidthNanos) {
        long nbEpochsPerMin = ((long) 60 * 1000 * 1000000) / epochWidthNanos;
        return Double.valueOf(line[VM_COL]) * nbEpochsPerMin;
    }
}