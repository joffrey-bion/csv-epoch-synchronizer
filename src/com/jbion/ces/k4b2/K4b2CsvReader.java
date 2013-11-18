package com.jbion.ces.k4b2;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import com.jbion.utils.csv.CsvReader;
import com.jbion.utils.csv.Csv.NotACsvFileException;

/**
 * A CSV reader for the files produced by the Cosmed K4b2.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
class K4b2CsvReader extends CsvReader {

    private static final int NB_HEADER_LINES = 3;

    private Sample lastReadSample;

    /**
     * Creates a new {@link K4b2CsvReader} for the specified file.
     * 
     * @param filename
     *            The path to the file to read.
     * @throws FileNotFoundException
     *             If the specified file does not exist.
     * @throws NotACsvFileException
     *             If the specified file is not a CSV file.
     */
    public K4b2CsvReader(String filename) throws FileNotFoundException, NotACsvFileException {
        super(filename);
    }

    /**
     * Skips the headers of the CSV file, so that the next row to read is a data row.
     * 
     * @throws IOException
     *             If an error occurs while reading the file.
     */
    public void skipHeaders() throws IOException {
        for (int i = 0; i < NB_HEADER_LINES; i++) {
            super.readRow();
        }
    }

    /**
     * Reads a row as a {@link Sample} object.
     * 
     * @return A {@link Sample} object representing the CSV row.
     * @throws IOException
     *             If an error occurs while reading the file.
     */
    public Sample readK4b2Sample() throws IOException {
        String[] row = readRow();
        if (row == null) {
            return null;
        }
        try {
            long previousTime = lastReadSample != null ? lastReadSample.endTime : 0;
            lastReadSample = new Sample(row, previousTime);
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
        return lastReadSample;
    }

    /**
     * Skips the next {@code nbToSkip} markers (including the current one if the
     * current row is marked) and goes to the next marked row.
     * 
     * @param nbToSkip
     *            The number of markers that have to be skipped.
     * @return The next marked row after the {@code nbToSkip} skipped markers.
     * @throws IOException
     *             If an error occurs while reading the file.
     */
    public Sample skipMarkers(int nbToSkip) throws IOException {
        if (lastReadSample == null || !lastReadSample.isMarked()) {
            goToNextMarker();
        }
        // at this point the current row is marked
        for (int i = 0; i < nbToSkip; i++) {
            goToNextMarker();
        }
        return lastReadSample;
    }

    /**
     * Reads as many rows as necessary to reach the next row with a marker. If the
     * current row is marked, it is not taken into account and the next row with a
     * marker is returned.
     * 
     * @return The next marked sample.
     * @throws IOException
     *             If an error occurs while reading the file.
     */
    public Sample goToNextMarker() throws IOException {
        int count = 0;
        while (readK4b2Sample() != null) {
            count++;
            if (lastReadSample.isMarked()) {
                return lastReadSample;
            }
        }
        throw new EOFException(
                "The end of file has been reached while looking for the next marker (" + count
                        + " rows read).");
    }

    /**
     * Returns the last sample that was read by this reader.
     * 
     * @return the last sample that was read by this reader.
     */
    public Sample getLastSample() {
        return lastReadSample;
    }
}
