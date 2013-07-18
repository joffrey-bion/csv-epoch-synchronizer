package com.joffrey_bion.csv_epoch_synchronizer.phone;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import com.joffrey_bion.csv.Csv;
import com.joffrey_bion.csv.TimestampedCsvReader;
import com.joffrey_bion.utils.dates.DateHelper;

public class PhoneCsvReader extends TimestampedCsvReader {

    private static final int TIMESTAMP_COL = 0;
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public PhoneCsvReader(String filename) throws FileNotFoundException, Csv.NotACsvFileException {
        super(filename);
    }

    @Override
    public long extractTimestamp(String[] line) throws IOException {
        try {
            return DateHelper.timestampStrToNanos(line[TIMESTAMP_COL], TIMESTAMP_FORMAT);
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }

}
