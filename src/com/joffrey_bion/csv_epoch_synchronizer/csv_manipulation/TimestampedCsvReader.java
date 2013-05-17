package com.joffrey_bion.csv_epoch_synchronizer.csv_manipulation;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class TimestampedCsvReader extends CsvReader {

    public TimestampedCsvReader(String filename) throws FileNotFoundException {
        super(filename);
    }

    public abstract long extractTimestamp(String[] line) throws IOException;

    public void skipToReachTimestamp(long timestamp) throws IOException {
        long currTimestamp;
        in.mark(200);
        String[] line;
        while ((line = readRow()) != null) {
            currTimestamp = extractTimestamp(line);
            if (currTimestamp >= timestamp) {
                in.reset();
                break;
            }
            in.mark(200);
        }
    }
}
