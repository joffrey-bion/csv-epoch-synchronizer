package com.jbion.ces.actigraph;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import com.jbion.utils.csv.Csv;
import com.jbion.utils.csv.TimestampedCsvReader;
import com.jbion.utils.dates.DateHelper;

/**
 * A reader for the CSV file produced by ActiLife when exporting the actigraph's
 * epochs. All the constants relative to the organization of the CSV file are
 * hardcoded in this class.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class ActigraphCsvReader extends TimestampedCsvReader {

    private final ActigraphFileFormat f;

    /**
     * Opens a reader for the specified actigraph CSV file.
     * 
     * @param filename
     *            The path to the CSV file to read.
     * @param actigraphFileFormat
     *            The format to expect from the specified file.
     * @throws FileNotFoundException
     *             If the specified file does not exist.
     * @throws Csv.NotACsvFileException
     *             If the specified file is not a CSV file.
     */
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

    /**
     * Returns the number of counts per minute in the specified row of the CSV file.
     * 
     * @param line
     *            The line to get the CPM from.
     * @param epochWidthNanos
     *            The width of the actigraph's epochs, to scale the CPM to 1 minute.
     * @return the CPM of the specified row.
     */
    public double extractCountsPerMinutes(String[] line, long epochWidthNanos) {
        long nbEpochsPerMin = ((long) 60 * 1000 * 1000000) / epochWidthNanos;
        return Double.valueOf(line[f.VM_COL]) * nbEpochsPerMin;
    }

    /**
     * Skips the headers of the actigraph CSV file read by this reader.
     * 
     * @throws IOException
     *             If any I/O error occurs.
     */
    public void skipHeaders() throws IOException {
        readRows(f.NB_HEADER_LINES);
    }
}
