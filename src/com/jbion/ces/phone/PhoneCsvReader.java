package com.jbion.ces.phone;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import com.jbion.utils.csv.Csv;
import com.jbion.utils.csv.TimestampedCsvReader;
import com.jbion.utils.dates.DateHelper;

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