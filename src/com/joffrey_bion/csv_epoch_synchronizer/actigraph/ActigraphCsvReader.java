package com.joffrey_bion.csv_epoch_synchronizer.actigraph;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import com.joffrey_bion.csv.Csv;
import com.joffrey_bion.csv.TimestampedCsvReader;
import com.joffrey_bion.utils.dates.DateHelper;

/**
 * A reader for the CSV file produced by ActiLife when exporting the actigraph's
 * epochs. All the constants relative to the organization of the CSV file are
 * hardcoded in this class.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class ActigraphCsvReader extends TimestampedCsvReader {

    private final ActigraphFileFormat f;

    public ActigraphCsvReader(String filename, ActigraphFileFormat actigraphFileFormat)
            throws FileNotFoundException, Csv.NotACsvFileException {
        super(filename);
        this.f = actigraphFileFormat;
    }

    @Override
    public long extractTimestamp(String[] line) throws IOException {
        String timestamp = line[f.DATE_COL] + " " + line[f.TIME_COL];
        String format = f.DATE_FORMAT + " " + f.TIME_FORMAT;
        try {
            return DateHelper.timestampStrToNanos(timestamp, format);
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }

    public double extractCountsPerMinutes(String[] line, long epochWidthNanos) {
        long nbEpochsPerMin = ((long) 60 * 1000 * 1000000) / epochWidthNanos;
        return Double.valueOf(line[f.VM_COL]) * nbEpochsPerMin;
    }

    public void skipHeaders() throws IOException {
        readRows(f.NB_HEADER_LINES);
    }
}
