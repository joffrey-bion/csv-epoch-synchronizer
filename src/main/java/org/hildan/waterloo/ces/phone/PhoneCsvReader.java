package org.hildan.waterloo.ces.phone;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.hildan.utils.csv.NotACsvFileException;
import org.hildan.utils.csv.TimestampedCsvReader;
import org.hildan.utils.dates.DateHelper;

public class PhoneCsvReader extends TimestampedCsvReader {

    private static final int TIMESTAMP_COL = 0;

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public PhoneCsvReader(String filename) throws FileNotFoundException, NotACsvFileException {
        super(filename);
    }

    @Override
    public long extractTimestamp(String[] line) throws IOException {
        try {
            return DateHelper.timestampStrToNanos(line[TIMESTAMP_COL], TIMESTAMP_FORMAT);
        } catch (final ParseException e) {
            throw new IOException(e.getMessage());
        }
    }

}
